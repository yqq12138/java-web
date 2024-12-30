package cn.example.shopping.service;


import cn.example.shopping.model.Product;
import cn.example.shopping.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<Product> getProductsByCategory(String category) {
        System.out.println("查询类目: " + category);
        
        if ("all".equalsIgnoreCase(category)) {
            return productRepository.findAll();
        }
        
        // 使用精确匹配查询
        return productRepository.findByCategoryExact(category);
    }

    public List<Product> searchProducts(String keyword) {
        System.out.println("搜索关键词: " + keyword);
        // 在商品标题中搜索
        return productRepository.findByTitleContainingIgnoreCase(keyword);
    }
} 