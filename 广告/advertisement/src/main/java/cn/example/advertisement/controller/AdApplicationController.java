package cn.example.advertisement.controller;

import cn.example.advertisement.model.AdApplication;
import cn.example.advertisement.service.AdApplicationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequestMapping("/api/ad-applications")
@CrossOrigin
public class AdApplicationController {

    @Autowired
    private AdApplicationService applicationService;

    //广告主申请获得广告投放权限
    @PostMapping
    public ResponseEntity<?> submitApplication(@RequestBody AdApplication application,
                                               HttpServletRequest request) {
        try {
            // 设置用户ID为客户端IP
            application.setUserId(application.getUserId());  // 使用前端传来的userId
            // 设置初始状态
            application.setStatus("PENDING");
            return ResponseEntity.ok(applicationService.submit(application));
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    // 管理员获取广告主的申请
    @GetMapping
    public ResponseEntity<?> getApplications(@RequestParam(required = false) String userId) {
        try {
            if (userId != null) {
                List<AdApplication> applications = applicationService.findByUserId(userId);
                return ResponseEntity.ok(applications);
            } else {
                // 管理员获取所有申请
                List<AdApplication> allApplications = applicationService.findAll();
                return ResponseEntity.ok(allApplications);
            }
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    // 批准广告主的申请
    @PutMapping("/{id}/approve")
    public ResponseEntity<?> approveApplication(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(applicationService.approve(id));
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    // 拒绝广告主的申请
    @PutMapping("/{id}/reject")
    public ResponseEntity<?> rejectApplication(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(applicationService.reject(id));
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }

    // 获取广告主的申请状态
    @GetMapping("/status")
    public ResponseEntity<?> getApplicationStatus(@RequestParam(required = false) String userId) {
        try {
            if (userId == null) {
                Map<String, Object> response = new HashMap<>();
                response.put("status", "NOT_APPLIED");
                response.put("userId", null);
                return ResponseEntity.ok(response);
            }

            AdApplication application = applicationService.findLatestByUserId(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("status", application != null ? application.getStatus() : "NOT_APPLIED");
            response.put("userId", userId);

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(response);
        }
    }
}