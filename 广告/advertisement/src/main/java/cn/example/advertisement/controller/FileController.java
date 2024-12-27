package cn.example.advertisement.controller;

import cn.example.advertisement.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        String url = fileService.storeFile(file);
        Map<String, String> response = new HashMap<>();
        response.put("url", url);
        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<?> updateFile(@RequestParam("file") MultipartFile file) {
        return uploadFile(file);
    }
} 