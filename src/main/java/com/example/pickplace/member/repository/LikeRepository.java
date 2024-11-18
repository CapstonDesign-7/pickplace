package com.example.pickplace.member.repository;

import com.example.pickplace.member.repository.entity.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByMemberIdAndReviewId(String memberId, Long reviewId);
    boolean existsByMemberIdAndReviewId(String memberId, Long reviewId);
}
