package cn.example.shopping.controller;


import cn.example.shopping.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
@CrossOrigin(origins = {"http://localhost:5174", "http://127.0.0.1:5174"})
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping("/category/{category}")
    public ResponseEntity<?> getProductsByCategory(@PathVariable String category) {
        try {
            return ResponseEntity.ok(productService.getProductsByCategory(category));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("获取商品列表失败: " + e.getMessage());
        }
    }

    @GetMapping("/search")
    public ResponseEntity<?> searchProducts(@RequestParam String keyword) {
        try {
            return ResponseEntity.ok(productService.searchProducts(keyword));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("搜索商品失败: " + e.getMessage());
        }
    }
} 