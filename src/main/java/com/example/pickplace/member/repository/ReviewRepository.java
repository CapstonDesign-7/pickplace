package com.example.pickplace.member.repository;

import com.example.pickplace.member.repository.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByMemberId(String memberId);

    // 좋아요 수로 정렬하여 모든 리뷰 조회
    @Query("SELECT r FROM Review r LEFT JOIN r.likes l GROUP BY r ORDER BY COUNT(l) DESC")
    List<Review> findAllOrderByLikesCountDesc();

    // 생성일자로 정렬하여 모든 리뷰 조회
    @Query("SELECT r FROM Review r ORDER BY r.createdAt DESC")
    List<Review> findAllOrderByCreatedAtDesc();

    // 지역으로 리뷰 검색
    @Query("SELECT r FROM Review r WHERE r.region LIKE %:region%")
    List<Review> findByRegionContaining(@Param("region") String region);
}
