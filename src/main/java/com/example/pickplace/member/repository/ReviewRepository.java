package com.example.pickplace.member.repository;

import com.example.pickplace.member.repository.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByMemberId(String memberId);

    // 좋아요 수로 정렬하여 모든 리뷰 조회
    @Query("SELECT r FROM Review r LEFT JOIN r.likes l GROUP BY r ORDER BY COUNT(l) DESC")
    List<Review> findAllOrderByLikesCountDesc();

    // 좋아요 수와 생성일자로 정렬하여 모든 리뷰 조회
    @Query("SELECT r FROM Review r LEFT JOIN r.likes l " +
            "GROUP BY r " +
            "ORDER BY COUNT(l) DESC, r.createdAt DESC")
    List<Review> findAllOrderByLikesCountAndCreatedAtDesc();
}
