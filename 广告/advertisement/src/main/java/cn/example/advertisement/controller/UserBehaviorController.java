package cn.example.advertisement.controller;

import cn.example.advertisement.model.UserBehavior;
import cn.example.advertisement.service.UserBehaviorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/track")
@CrossOrigin(
        origins = {"http://localhost:5174", "http://127.0.0.1:5174"}, //TODO: 替换为前段应用的实际域名
        allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS},
        maxAge = 3600
)
public class UserBehaviorController {

    @Autowired
    private UserBehaviorService behaviorService;

    @PostMapping
    public ResponseEntity<?> trackBehavior(@RequestBody UserBehavior behavior) {
        try {
            // 打印详细的接收数据
            System.out.println("\n========== 收到用户行为数据 ==========");
            System.out.println("访客ID: " + behavior.getVisitorId());
            System.out.println("行为类型: " + behavior.getAction());
            System.out.println("商品类目: " + behavior.getCategory());
            System.out.println("性别偏好: " + behavior.getGender());
            System.out.println("价格: " + behavior.getPrice());
            System.out.println("浏览器: " + behavior.getBrowser());
            System.out.println("平台: " + behavior.getPlatform());
            System.out.println("语言: " + behavior.getLanguage());
            System.out.println("分辨率: " + behavior.getScreenResolution());
            System.out.println("时区: " + behavior.getTimezone());
            System.out.println("原始数据: " + behavior.toString());
            System.out.println("====================================\n");

            UserBehavior saved = behaviorService.save(behavior);
            System.out.println("数据已保存到数据库，ID: " + saved.getId());

            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            System.err.println("保存用户行为数据时发生错误:");
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to track behavior: " + e.getMessage());
        }
    }

    @GetMapping("/visitor/{visitorId}")
    public ResponseEntity<?> getVisitorBehaviors(@PathVariable String visitorId) {
        try {
            return ResponseEntity.ok(behaviorService.findByVisitorId(visitorId));
        } catch (Exception e) {
            System.err.println("查询用户行为数据时发生错误:");
            e.printStackTrace();
            return ResponseEntity.badRequest().body("Failed to get behaviors: " + e.getMessage());
        }
    }
}