package cn.example.advertisement.service;


import cn.example.advertisement.model.UserBehavior;
import cn.example.advertisement.model.UserPreference;
import cn.example.advertisement.repository.UserBehaviorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserBehaviorService {

    @Autowired
    private UserBehaviorRepository behaviorRepository;

    public UserBehavior save(UserBehavior behavior) {
        return behaviorRepository.save(behavior);
    }

    public List<UserBehavior> findByVisitorId(String visitorId) {
        // 确保visitorId格式正确
        String fullVisitorId = visitorId.startsWith("visitor_") ? visitorId : "visitor_" + visitorId;
        
        // 使用完整的visitor_id查询
        List<UserBehavior> behaviors = behaviorRepository.findBehaviorsByFullVisitorId(fullVisitorId);
        
        if (behaviors.isEmpty()) {
            behaviors = behaviorRepository.findByVisitorId(visitorId);
        }
        
        return behaviors;
    }
    
    // 获取用户的偏好数据
    public UserPreference getUserPreference(String visitorId) {
        List<UserBehavior> behaviors = findByVisitorId(visitorId);
        UserPreference preference = new UserPreference();
        
        for (UserBehavior behavior : behaviors) {
            // 更新类目偏好
            if (behavior.getCategory() != null) {
                preference.addCategory(behavior.getCategory());
            }
            
            // 更新性别偏好
            if (behavior.getGender() != null) {
                preference.addGender(behavior.getGender());
            }
            
            // 更新价格偏好
            if (behavior.getPrice() != null) {
                preference.addPrice(behavior.getPrice());
            }
        }
        
        return preference;
    }
} 