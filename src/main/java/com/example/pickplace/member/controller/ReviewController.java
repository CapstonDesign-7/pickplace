package com.example.pickplace.member.controller;

import com.example.pickplace.member.controller.dto.ReviewRequest;
import com.example.pickplace.member.response.ApiResponse;
import com.example.pickplace.member.response.ReviewResponse;
import com.example.pickplace.member.service.ReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
@Slf4j
public class ReviewController {

    private final ReviewService reviewService;

    // 리뷰 작성
    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(
            Authentication authentication,
            @RequestParam("scheduleId") Long scheduleNo,  // @RequestParam으로 변경
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "images", required = false) List<MultipartFile> images) {

        String memberId = authentication.getName();

        ReviewRequest request = new ReviewRequest();
        request.setScheduleNo(scheduleNo);
        request.setTitle(title);
        request.setContent(content);

        return ResponseEntity.ok(reviewService.createReview(memberId, request, images));
    }

    // 리뷰 업데이트
    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> updateReview(
            Authentication authentication,
            @PathVariable(name = "reviewId") Long reviewId,
            @ModelAttribute ReviewRequest request,
            @RequestParam(value = "images", required = false) List<MultipartFile> images) {
        String memberId = authentication.getName();
        return ResponseEntity.ok(reviewService.updateReview(memberId, reviewId, request, images));
    }

    // 리뷰 삭제
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<ApiResponse> deleteReview(
            Authentication authentication,
            @PathVariable(name = "reviewId") Long reviewId) {
        String memberId = authentication.getName();
        reviewService.deleteReview(memberId, reviewId);
        return ResponseEntity.ok(new ApiResponse("리뷰가 성공적으로 삭제되었습니다.", true));
    }

    // 리뷰 수정
    @GetMapping("/{reviewId}/edit")
    public ResponseEntity<ReviewResponse> getReviewForEdit(
            Authentication authentication,
            @PathVariable(name = "reviewId") Long reviewId) {
        String memberId = authentication.getName();
        return ResponseEntity.ok(reviewService.getReviewForEdit(memberId, reviewId));
    }

    // 전체 리뷰 가져오기 (최신순)
    @GetMapping
    public ResponseEntity<List<ReviewResponse>> getAllReviews(Authentication authentication) {
        String currentUserId = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(reviewService.getAllReviews(currentUserId));
    }

    // 추천순으로 정렬된 리뷰 목록
    @GetMapping("/sort/likes")
    public ResponseEntity<List<ReviewResponse>> getReviewsSortedByLikes(Authentication authentication) {
        String currentUserId = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(reviewService.getReviewsSortedByLikes(currentUserId));
    }

    // 특정 리뷰 가져오기(리뷰 수정 페이지에서 기존 정보 로딩, 상세 페이지 조회)
    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> getReview(
            @PathVariable(name = "reviewId") Long reviewId,
            Authentication authentication) {
        String currentUserId = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(reviewService.getReview(reviewId, currentUserId));
    }

    // 내 리뷰 가져오기
    @GetMapping("/my")
    public ResponseEntity<List<ReviewResponse>> getMyReviews(Authentication authentication) {
        String memberId = authentication.getName();
        return ResponseEntity.ok(reviewService.getMyReviews(memberId));
    }

    // 좋아요 기능
    @PostMapping("/{reviewId}/like")
    public ResponseEntity<Map<String, Object>> toggleLike(
            Authentication authentication,
            @PathVariable(name = "reviewId") Long reviewId) {

        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("message", "로그인이 필요합니다."));
        }

        String memberId = authentication.getName();
        reviewService.toggleLike(memberId, reviewId);

        Map<String, Object> response = new HashMap<>();
        response.put("isLiked", reviewService.isLikedByMember(memberId, reviewId));
        response.put("likeCount", reviewService.getLikeCount(reviewId));

        return ResponseEntity.ok(response);
    }

    // 리뷰 검색
    @GetMapping("/search")
    public ResponseEntity<List<ReviewResponse>> searchReviews(
            @RequestParam(name = "query") String query,
            @RequestParam(name = "type") String type,
            @RequestParam(name = "sortType", defaultValue = "latest") String sortType,
            Authentication authentication) {
        String currentUserId = authentication != null ? authentication.getName() : null;

        List<ReviewResponse> reviews;
        if ("region".equals(type)) {
            reviews = reviewService.searchReviewsByRegion(query, currentUserId, sortType);
        } else if ("title".equals(type)) {
            reviews = reviewService.searchReviewsByTitle(query, currentUserId, sortType);
        } else {
            throw new IllegalArgumentException("Invalid search type");
        }

        return ResponseEntity.ok(reviews);
    }
}