package cn.example.shopping.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileService {
    
    private final String uploadDir = "uploads";  // 改回固定路径
    
    public FileService() {
        // 创建上传目录
        new File(uploadDir).mkdirs();
    }
    
    public String storeFile(MultipartFile file) {
        try {
            // 生成唯一文件名
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path targetPath = Paths.get(uploadDir).resolve(fileName);
            
            // 保存文件
            Files.copy(file.getInputStream(), targetPath);
            
            return "/uploads/" + fileName;
        } catch (Exception e) {
            throw new RuntimeException("文件上传失败", e);
        }
    }
} 