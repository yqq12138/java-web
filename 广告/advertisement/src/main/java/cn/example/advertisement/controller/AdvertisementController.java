package cn.example.advertisement.controller;

import cn.example.advertisement.model.Advertisement;
import cn.example.advertisement.service.AdvertisementService;
import cn.example.advertisement.service.UserBehaviorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/advertisements")
@CrossOrigin(origins = {"http://localhost:5174", "http://127.0.0.1:5174"})
public class AdvertisementController {

    @Autowired
    private AdvertisementService advertisementService;

    @Autowired
    private UserBehaviorService behaviorService;

    // 分页获取所有广告
    @GetMapping
    public ResponseEntity<?> getAllAdvertisements(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            return ResponseEntity.ok(advertisementService.getAllAdvertisements(PageRequest.of(page, size)));
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    // 分页获取广告主的广告列表
    @GetMapping("/my")
    public ResponseEntity<?> getMyAdvertisements(
            @RequestParam(required = true) String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            return ResponseEntity.ok(advertisementService.getAdvertisementsByUserId(userId, PageRequest.of(page, size)));
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    // 创建新的广告
    @PostMapping
    public ResponseEntity<?> createAdvertisement(@RequestBody Advertisement advertisement) {
        try {
            return ResponseEntity.ok(advertisementService.createAdvertisement(advertisement));
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    // 更新广告
    @PutMapping("/{id}")
    public ResponseEntity<?> updateAdvertisement(
            @PathVariable String id,
            @RequestBody Advertisement advertisement) {
        try {
            advertisement.setId(id);
            return ResponseEntity.ok(advertisementService.updateAdvertisement(advertisement));
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    // 删除广告
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAdvertisement(@PathVariable String id) {
        try {
            advertisementService.deleteAdvertisement(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "删除成功");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    // 获取个性化广告
    @GetMapping("/recommend")
    public ResponseEntity<?> getPersonalizedAds(@RequestParam(required = false) String visitorId) {
        try {
            System.out.println("\n========== 开始获取个性化广告 ==========");
            System.out.println("访客ID: " + visitorId);

            if (visitorId == null || visitorId.isEmpty()) {
                System.out.println("访客ID为空，返回默认广告");
                return ResponseEntity.ok(advertisementService.getDefaultAds());
            }

            // 根据访客ID获取个性化广告
            List<Advertisement> ads = advertisementService.getPersonalizedAds(visitorId);
            System.out.println("获取到的广告数量: " + ads.size());

            // 控制台打印广告详情
            ads.forEach(ad -> {
                System.out.println("\n广告详情:");
                System.out.println("ID: " + ad.getId());
                System.out.println("标题: " + ad.getTitle());
                System.out.println("类目: " + ad.getCategory());
                System.out.println("性别: " + ad.getGender());
                System.out.println("价格: " + ad.getPrice());
            });

            System.out.println("\n========== 个性化广告获取完成 ==========\n");
            return ResponseEntity.ok(ads);
        } catch (Exception e) {
            System.err.println("获取个性化广告时发生错误:");
            e.printStackTrace();
            return ResponseEntity.badRequest().body("获取个性化广告失败: " + e.getMessage());
        }
    }
}
