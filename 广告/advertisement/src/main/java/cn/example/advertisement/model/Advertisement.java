package cn.example.advertisement.model;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "advertisements")
public class Advertisement {
    @Id
    private String id;
    
    @Column(name = "user_id")
    private String userId;
    
    private String title;
    private String imageUrl;
    private BigDecimal price;
    private String category;
    private String gender;
    
    @Setter
    @Getter
    @Transient  // 不持久化到数据库
    private Double recommendScore;
    
    @Column(name = "create_time")
    private LocalDateTime createTime = LocalDateTime.now();

}