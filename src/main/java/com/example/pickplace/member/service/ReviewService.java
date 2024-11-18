package com.example.pickplace.member.service;

import com.example.pickplace.member.controller.dto.ReviewRequest;
import com.example.pickplace.member.response.ReviewResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ReviewService {
    // 리뷰 작성 및 업데이트, 삭제 서비스
    ReviewResponse createReview(String memberId, ReviewRequest request, List<MultipartFile> images);
    ReviewResponse updateReview(String memberId, Long reviewId, ReviewRequest request, List<MultipartFile> images);
    void deleteReview(String memberId, Long reviewId);

    // 리뷰 불러오기
    int getLikeCount(Long reviewId);
    ReviewResponse getReview(Long reviewId, String currentUserId);
    List<ReviewResponse> getMyReviews(String memberId);
    List<ReviewResponse> getAllReviews(String currentUserId);

    // 좋아요 기능
    void toggleLike(String memberId, Long reviewId);
    boolean isLikedByMember(String memberId, Long reviewId);
}