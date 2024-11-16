package com.example.pickplace.member.service;

import com.example.pickplace.member.controller.dto.ReviewRequest;
import com.example.pickplace.member.response.ReviewResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ReviewService {
    ReviewResponse createReview(String memberId, ReviewRequest request, List<MultipartFile> images);
    ReviewResponse updateReview(String memberId, Long reviewId, ReviewRequest request, List<MultipartFile> images);
    void deleteReview(String memberId, Long reviewId);
    ReviewResponse getReview(Long reviewId);
    List<ReviewResponse> getMyReviews(String memberId);
    List<ReviewResponse> getAllReviews();
}