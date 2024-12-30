package cn.example.advertisement.service;


import cn.example.advertisement.model.Advertisement;
import cn.example.advertisement.model.UserBehavior;
import cn.example.advertisement.repository.AdvertisementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdvertisementService {

    @Autowired
    private AdvertisementRepository advertisementRepository;
    
    @Autowired
    private UserBehaviorService behaviorService;

    //分页获取所有广告
    public Page<Advertisement> getAllAdvertisements(Pageable pageable) {
        return advertisementRepository.findAll(pageable);
    }

    // 分页获取某广告主的广告列表
    public Page<Advertisement> getAdvertisementsByUserId(String userId, Pageable pageable) {
        return advertisementRepository.findByUserId(userId, pageable);
    }

    // 创建新广告
    public Advertisement createAdvertisement(Advertisement advertisement) {
        if (advertisement.getTitle() == null || advertisement.getTitle().trim().isEmpty()) {
            throw new RuntimeException("标题不能为空");
        }
        if (advertisement.getPrice() == null) {
            throw new RuntimeException("价格不能为空");
        }
        if (advertisement.getCategory() == null || advertisement.getCategory().trim().isEmpty()) {
            throw new RuntimeException("类别不能为空");
        }
        if (advertisement.getGender() == null || advertisement.getGender().trim().isEmpty()) {
            throw new RuntimeException("性别不能为空");
        }
        if (advertisement.getImageUrl() == null || advertisement.getImageUrl().trim().isEmpty()) {
            throw new RuntimeException("图片不能为空");
        }
        if (advertisement.getUserId() == null || advertisement.getUserId().trim().isEmpty()) {
            throw new RuntimeException("用户ID不能为空");
        }
        
        // 生成广告ID：用户名_时间戳
        String adId = advertisement.getUserId() + "_" + System.currentTimeMillis();
        advertisement.setId(adId);
        
        return advertisementRepository.save(advertisement);
    }

    // 根据ID获取广告
    public Advertisement updateAdvertisement(Advertisement advertisement) {
        if (!advertisementRepository.existsById(advertisement.getId())) {
            throw new RuntimeException("广告不存在");
        }
        return advertisementRepository.save(advertisement);
    }

    // 根据ID删除广告
    public void deleteAdvertisement(String id) {
        if (!advertisementRepository.existsById(id)) {
            throw new RuntimeException("广告不存在");
        }
        advertisementRepository.deleteById(id);
    }

    // 获取默认广告
    public List<Advertisement> getDefaultAds() {
        return advertisementRepository.findAll().stream()
            .limit(5)
            .collect(Collectors.toList());
    }

    // 根据用户行为获取个性化广告
    public List<Advertisement> getPersonalizedAds(String visitorId) {
        System.out.println("========== 开始获取个性化广告 ==========");
        System.out.println("访客ID: " + visitorId);
        
        // 获取用户最近10条有效行为记录(价格不为空)
        List<UserBehavior> behaviors = behaviorService.findByVisitorId(visitorId);
        System.out.println("获取到的用户行为数量: " + behaviors.size());
        
        // 如果没有用户行为数据，返回所有广告并随机排序
        if (behaviors.isEmpty()) {
            System.out.println("没有用户行为数据，返回随机广告");
            List<Advertisement> allAds = advertisementRepository.findAll();
            if (allAds.isEmpty()) {
                System.out.println("没有广告数据，返回默认广告");
                Advertisement defaultAd = new Advertisement();
                defaultAd.setId("default_1");
                defaultAd.setTitle("默认广告");
                defaultAd.setPrice(new BigDecimal("99.99"));
                defaultAd.setCategory("other");
                defaultAd.setGender("ALL");
                defaultAd.setImageUrl("/placeholder.svg?height=200&width=200");
                return Collections.singletonList(defaultAd);
            }
            Collections.shuffle(allAds);
            return allAds.stream().limit(5).collect(Collectors.toList());
        }
        
        // 过滤和排序行为数据，获取最近的10条有效行为记录
        behaviors = behaviors.parallelStream()
            .filter(b -> b.getPrice() != null) //TODO: 针对于购物系统的价格字段，如果是新闻系统，则没有这行过滤
            .sorted(Comparator.comparing(UserBehavior::getCreateTime).reversed())
            .limit(10)
            .toList();
        
        System.out.println("处理后的用户行为数量: " + behaviors.size());
        
        // 使用Map来优化统计计算
        Map<String, Integer> categoryCount = new HashMap<>();
        Map<String, Integer> genderCount = new HashMap<>();
        double totalPrice = 0;
        int priceCount = 0;
        
        // 一次遍历完成所有统计
        for (UserBehavior behavior : behaviors) {
            if (behavior.getCategory() != null) {
                System.out.println("用户行为类目: " + behavior.getCategory());
                categoryCount.merge(behavior.getCategory(), 1, Integer::sum);
            }
            if (behavior.getGender() != null) {
                System.out.println("用户行为性别: " + behavior.getGender());
                genderCount.merge(behavior.getGender(), 1, Integer::sum);
            }
            if (behavior.getPrice() != null) {
                System.out.println("用户行为价格: " + behavior.getPrice());
                totalPrice += behavior.getPrice();
                priceCount++;
            }
        }
        
        // 计算用户偏好
        String preferredCategory = categoryCount.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);
            
        String preferredGender = genderCount.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);
            
        double avgPrice = priceCount > 0 ? totalPrice / priceCount : 0.0;
            
        System.out.println("首选类目: " + preferredCategory);
        System.out.println("首选性别: " + preferredGender);
        System.out.println("平均价格: " + avgPrice);
        
        // 计算价格区间 - 使用更灵活的计算方式
        double percentageRange = avgPrice * 0.5;  // 基于百分比的范围
        double fixedRange = 50.0;  // 固定范围
        double minPrice = Math.max(0, avgPrice - Math.max(percentageRange, fixedRange));
        double maxPrice = avgPrice + Math.max(percentageRange, fixedRange);
        
        System.out.println("价格区间: " + minPrice + " - " + maxPrice);

        // 获取符合条件的广告
        List<Advertisement> matchingAds;
        if (preferredCategory != null && preferredGender != null) {
            System.out.println("尝试按类目和性别匹配广告: " + preferredCategory + ", " + preferredGender);
            // 首先尝试获取价格区间内的广告
            matchingAds = advertisementRepository.findByCategoryAndGenderAndPriceBetween(
                preferredCategory, 
                preferredGender,
                new BigDecimal(minPrice),
                new BigDecimal(maxPrice)
            );
            System.out.println("价格区间内的广告数量: " + matchingAds.size());
            
            // 如果价格区间内没有广告，再尝试只按类目和性别匹配
            if (matchingAds.isEmpty()) {
                matchingAds = advertisementRepository.findByCategoryAndGender(preferredCategory, preferredGender);
                System.out.println("类目和性别匹配的广告数量: " + matchingAds.size());
            }
            
            if (matchingAds.isEmpty()) {
                System.out.println("尝试只按类目匹配广告: " + preferredCategory);
                matchingAds = advertisementRepository.findByCategory(preferredCategory);
                System.out.println("类目匹配的广告数量: " + matchingAds.size());
            }
            
            if (matchingAds.isEmpty()) {
                System.out.println("获取所有广告");
                matchingAds = advertisementRepository.findAll();
                System.out.println("获取到的广告数量: " + matchingAds.size());
            }
        } else if (preferredCategory != null) {
            System.out.println("按类目和价格区间匹配广告: " + preferredCategory);
            matchingAds = advertisementRepository.findByCategoryAndPriceBetween(
                preferredCategory,
                new BigDecimal(minPrice),
                new BigDecimal(maxPrice)
            );
            if (matchingAds.isEmpty()) {
                matchingAds = advertisementRepository.findByCategory(preferredCategory);
            }
        } else if (preferredGender != null) {
            System.out.println("按性别和价格区间匹配广告: " + preferredGender);
            matchingAds = advertisementRepository.findByGenderAndPriceBetween(
                preferredGender,
                new BigDecimal(minPrice),
                new BigDecimal(maxPrice)
            );
            if (matchingAds.isEmpty()) {
                matchingAds = advertisementRepository.findByGender(preferredGender);
            }
        } else {
            System.out.println("按价格区间匹配广告");
            matchingAds = advertisementRepository.findByPriceBetween(
                new BigDecimal(minPrice),
                new BigDecimal(maxPrice)
            );
            if (matchingAds.isEmpty()) {
                matchingAds = advertisementRepository.findAll();
            }
        }

        // 如果还是没有广告，创建一些默认广告
        if (matchingAds.isEmpty()) {
            Advertisement defaultAd = new Advertisement();
            defaultAd.setId("default_1");
            defaultAd.setTitle("默认广告");
            defaultAd.setPrice(new BigDecimal("99.99"));
            defaultAd.setCategory("默认");
            defaultAd.setGender("ALL");
            defaultAd.setImageUrl("/placeholder.svg?height=200&width=200");
            matchingAds = Collections.singletonList(defaultAd);
        }

        // 使用并行流计算广告评分
        Random random = new Random();
        List<Advertisement> personalizedAds = matchingAds.parallelStream()
            .map(ad -> {
                double score = 0.0;
                
                // 类目匹配 (权重 3.0)
                if (preferredCategory != null && preferredCategory.equals(ad.getCategory())) {
                    score += 3.0;
                }
                
                // 性别匹配 (权重 2.0)
                if (preferredGender != null && preferredGender.equals(ad.getGender())) {
                    score += 2.0;
                }
                
                // 价格匹配 (权重 6.0)
                if (ad.getPrice() != null && avgPrice > 0) {
                    double adPrice = ad.getPrice().doubleValue();
                    // 价格在目标区间内才给分
                    if (adPrice >= minPrice && adPrice <= maxPrice) {
                        // 价格越接近平均价格，得分越高
                        double priceDiff = Math.abs(adPrice - avgPrice) / avgPrice;
                        score += 6.0 * (1 - priceDiff);  // 最高6.0分
                    } else {
                        // 价格超出区间，根据超出程度降低分数
                        double priceRatio = adPrice / avgPrice;
                        if (priceRatio > 3.0 || priceRatio < 0.3) {
                            // 如果价格差距过大（超过3倍或小于0.3倍），分数接近于0
                            score *= 0.1;
                        } else if (priceRatio > 2.0 || priceRatio < 0.5) {
                            // 价格差距中等（2-3倍或0.3-0.5倍）
                            score *= 0.3;
                        } else {
                            // 价格差距较小（1.5-2倍或0.5-0.7倍）
                            score *= 0.5;
                        }
                    }
                }
                
                System.out.println("广告ID: " + ad.getId() + 
                    ", 类目: " + ad.getCategory() + 
                    ", 性别: " + ad.getGender() + 
                    ", 价格: " + ad.getPrice() + 
                    ", 得分: " + score);
                
                // 添加少量随机因素 (权重 0.5)
                score += random.nextDouble() * 0.5;
                
                ad.setRecommendScore(score);
                return ad;
            })
            .sorted(Comparator.comparing(Advertisement::getRecommendScore).reversed())
            .limit(5)
            .collect(Collectors.toList());
            
        System.out.println("========== 最终选择的广告 ==========");
        personalizedAds.forEach(ad -> 
            System.out.println("广告ID: " + ad.getId() + 
                ", 类目: " + ad.getCategory() + 
                ", 性别: " + ad.getGender() + 
                ", 价格: " + ad.getPrice() + 
                ", 最终得分: " + ad.getRecommendScore())
        );
        
        return personalizedAds;
    }
} 