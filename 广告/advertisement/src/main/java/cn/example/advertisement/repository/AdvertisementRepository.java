package cn.example.advertisement.repository;

import cn.example.advertisement.model.Advertisement;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface AdvertisementRepository extends JpaRepository<Advertisement, String> {
    Page<Advertisement> findByUserId(String userId, Pageable pageable);

    // 根据类目和性别查询广告
    @Query("SELECT a FROM Advertisement a WHERE a.category = :category AND a.gender = :gender")
    List<Advertisement> findByCategoryAndGender(@Param("category") String category, @Param("gender") String gender);

    // 根据类目查询广告
    @Query("SELECT a FROM Advertisement a WHERE a.category = :category")
    List<Advertisement> findByCategory(@Param("category") String category);

    // 根据性别查询广告
    @Query("SELECT a FROM Advertisement a WHERE a.gender = :gender")
    List<Advertisement> findByGender(@Param("gender") String gender);

    // 根据类目、性别和价格区间查询广告
    @Query("SELECT a FROM Advertisement a WHERE a.category = :category AND a.gender = :gender AND a.price BETWEEN :minPrice AND :maxPrice")
    List<Advertisement> findByCategoryAndGenderAndPriceBetween(
            @Param("category") String category,
            @Param("gender") String gender,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice
    );

    // 根据类目和价格区间查询广告
    @Query("SELECT a FROM Advertisement a WHERE a.category = :category AND a.price BETWEEN :minPrice AND :maxPrice")
    List<Advertisement> findByCategoryAndPriceBetween(
            @Param("category") String category,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice
    );

    // 根据性别和价格区间查询广告
    @Query("SELECT a FROM Advertisement a WHERE a.gender = :gender AND a.price BETWEEN :minPrice AND :maxPrice")
    List<Advertisement> findByGenderAndPriceBetween(
            @Param("gender") String gender,
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice
    );

    // 根据价格区间查询广告
    @Query("SELECT a FROM Advertisement a WHERE a.price BETWEEN :minPrice AND :maxPrice")
    List<Advertisement> findByPriceBetween(
            @Param("minPrice") BigDecimal minPrice,
            @Param("maxPrice") BigDecimal maxPrice
    );
}