package cn.example.advertisement.service;


import cn.example.advertisement.model.AdApplication;
import cn.example.advertisement.repository.AdApplicationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdApplicationService {
    
    @Autowired
    private AdApplicationRepository applicationRepository;


    //广告主提交申请
    public AdApplication submit(AdApplication application) {
        // 验证必填字段
        if (application.getStoreName() == null || application.getStoreName().trim().isEmpty()) {
            throw new RuntimeException("网店名称不能为空");
        }
        if (application.getStoreUrl() == null || application.getStoreUrl().trim().isEmpty()) {
            throw new RuntimeException("网店链接不能为空");
        }
        
        // 检查是否已有待审核的申请
        AdApplication existingApplication = findLatestByUserId(application.getUserId());
        if (existingApplication != null && "PENDING".equals(existingApplication.getStatus())) {
            throw new RuntimeException("您已有一个待审核的申请");
        }
        
        // 确保 userId 不超过数据库字段长度
        if (application.getUserId() != null && application.getUserId().length() > 50) {
            application.setUserId(application.getUserId().substring(0, 50));
        }
        
        return applicationRepository.save(application);
    }

    // 管理员获取所有申请
    public List<AdApplication> findByUserId(String userId) {
        return applicationRepository.findByUserId(userId);
    }

    // 管理员获取所有申请
    public List<AdApplication> findAll() {
        return applicationRepository.findAll();
    }

    // 管理员审核申请
    public AdApplication approve(Long id) {
        AdApplication application = applicationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("申请不存在"));
        application.setStatus("APPROVED");
        return applicationRepository.save(application);
    }

    // 管理员拒绝申请
    public AdApplication reject(Long id) {
        AdApplication application = applicationRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("申请不存在"));
        application.setStatus("REJECTED");
        return applicationRepository.save(application);
    }

    // 管理员获取最新的待审核的申请
    public AdApplication findLatestByUserId(String userId) {
        return applicationRepository.findFirstByUserIdOrderByCreateTimeDesc(userId);
    }
} 