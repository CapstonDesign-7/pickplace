package com.example.pickplace.member.service;

import com.example.pickplace.member.controller.dto.ReviewRequest;
import com.example.pickplace.member.response.ReviewResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ReviewService {
    // 리뷰 작성 및 업데이트, 삭제 서비스
    ReviewResponse createReview(String memberId, ReviewRequest request, List<MultipartFile> images);
    ReviewResponse updateReview(String memberId, Long reviewId, ReviewRequest request, List<MultipartFile> images);
    ReviewResponse getReviewForEdit(String memberId, Long reviewId);
    void deleteReview(String memberId, Long reviewId);

    // 리뷰 불러오기
    int getLikeCount(Long reviewId);
    ReviewResponse getReview(Long reviewId, String currentUserId);
    List<ReviewResponse> getMyReviews(String memberId);
    List<ReviewResponse> getAllReviews(String currentUserId);
    List<ReviewResponse> getReviewsSortedByLikes(String currentUserId); // 정렬(추천순)

    // 검색 기능
    List<ReviewResponse> searchReviewsByRegion(String region, String currentUserId, String sortType);
    List<ReviewResponse> searchReviewsByTitle(String title, String currentUserId, String sortType);

    // 좋아요 기능
    void toggleLike(String memberId, Long reviewId);
    boolean isLikedByMember(String memberId, Long reviewId);
}