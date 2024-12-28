package cn.example.shopping.model;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    private String category;
    private BigDecimal price;
    private String gender;
    
    @Column(name = "image_url")
    private String imageUrl;
    
    @Column(name = "create_time")
    private LocalDateTime createTime;
} 