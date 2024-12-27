package cn.example.advertisement.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class UserPreference {
    private Map<String, Integer> categoryPreferences = new HashMap<>();
    private Map<String, Integer> genderPreferences = new HashMap<>();
    private List<Double> priceHistory = new ArrayList<>();
    private double averagePrice = 0.0;

    public void addCategory(String category) {
        categoryPreferences.merge(category, 1, Integer::sum);
    }

    public void addGender(String gender) {
        genderPreferences.merge(gender, 1, Integer::sum);
    }

    public void addPrice(Double price) {
        priceHistory.add(price);
        // 只保留最近的10个价格
        if (priceHistory.size() > 10) {
            priceHistory.remove(0);
        }
        // 计算平均价格
        averagePrice = priceHistory.stream()
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0.0);
    }
} 