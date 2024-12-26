package cn.example.advertisement.repository;

import cn.example.advertisement.model.UserBehavior;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserBehaviorRepository extends JpaRepository<UserBehavior, Long> {
    List<UserBehavior> findByVisitorId(String visitorId);

    @Query(value = "SELECT * FROM user_behavior WHERE visitor_id = CONCAT('visitor_', :visitorId) ORDER BY create_time DESC", nativeQuery = true)
    List<UserBehavior> findBehaviorsWithNativeQuery(@Param("visitorId") String visitorId);

    @Query(value = "SELECT * FROM user_behavior WHERE visitor_id = :fullVisitorId ORDER BY create_time DESC", nativeQuery = true)
    List<UserBehavior> findBehaviorsByFullVisitorId(@Param("fullVisitorId") String fullVisitorId);
}