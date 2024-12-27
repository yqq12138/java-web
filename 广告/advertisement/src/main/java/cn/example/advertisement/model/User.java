package cn.example.advertisement.model;

import lombok.Data;

import jakarta.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    @Column(nullable = false)
    private String role;
    
    @Column(name = "create_time")
    private Date createTime = new Date();
} 