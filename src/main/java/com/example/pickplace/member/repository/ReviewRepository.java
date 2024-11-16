package com.example.pickplace.member.repository;

import com.example.pickplace.member.repository.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByMemberId(String memberId);
}
