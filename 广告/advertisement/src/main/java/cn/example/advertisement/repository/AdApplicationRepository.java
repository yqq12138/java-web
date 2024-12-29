package cn.example.advertisement.repository;


import cn.example.advertisement.model.AdApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface AdApplicationRepository extends JpaRepository<AdApplication, Long> {
    List<AdApplication> findByUserId(String userId);

    AdApplication findFirstByUserIdOrderByCreateTimeDesc(String userId);
}