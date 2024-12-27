package cn.example.advertisement.model;


import jakarta.persistence.*;
import lombok.Data;


import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "user_behavior")
public class UserBehavior {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "visitor_id")
    private String visitorId;
    
    private String action;
    private String category;
    private String gender;
    private Double price;
    
    @Column(columnDefinition = "TEXT")
    private String userAgent;
    
    private String browser;
    private String platform;
    private String language;
    private String screenResolution;
    private String timezone;
    
    @Column(name = "create_time")
    private LocalDateTime createTime = LocalDateTime.now();
} 