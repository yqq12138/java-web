package cn.example.advertisement.model;

import lombok.Data;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "ad_application")
public class AdApplication {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", length = 50)
    private String userId;
    
    @Column(name = "store_name")
    private String storeName;
    
    @Column(name = "store_url")
    private String storeUrl;
    
    @Column(name = "status")
    private String status = "PENDING";
    
    @Column(name = "create_time")
    private LocalDateTime createTime = LocalDateTime.now();
} 