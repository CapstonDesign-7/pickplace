package com.example.pickplace.member.controller;

import com.example.pickplace.member.controller.dto.ReviewRequest;
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

    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(
            Authentication authentication,
            @ModelAttribute ReviewRequest request,
            @RequestParam(value = "images", required = false) List<MultipartFile> images) {
        String memberId = authentication.getName();
        return ResponseEntity.ok(reviewService.createReview(memberId, request, images));
    }

    @PutMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> updateReview(
            Authentication authentication,
            @PathVariable Long reviewId,
            @RequestPart ReviewRequest request,
            @RequestPart(required = false) List<MultipartFile> images) {
        String memberId = authentication.getName();
        return ResponseEntity.ok(reviewService.updateReview(memberId, reviewId, request, images));
    }

    @DeleteMapping("/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            Authentication authentication,
            @PathVariable Long reviewId) {
        String memberId = authentication.getName();
        reviewService.deleteReview(memberId, reviewId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<ReviewResponse>> getAllReviews(Authentication authentication) {
        String currentUserId = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(reviewService.getAllReviews(currentUserId));
    }

    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> getReview(
            @PathVariable Long reviewId,
            Authentication authentication) {
        String currentUserId = authentication != null ? authentication.getName() : null;
        return ResponseEntity.ok(reviewService.getReview(reviewId, currentUserId));
    }

    @GetMapping("/my")
    public ResponseEntity<List<ReviewResponse>> getMyReviews(Authentication authentication) {
        String memberId = authentication.getName();
        return ResponseEntity.ok(reviewService.getMyReviews(memberId));
    }

    @PostMapping("/{reviewId}/like")
    public ResponseEntity<Map<String, Object>> toggleLike(
            Authentication authentication,
            @PathVariable(name = "reviewId") Long reviewId) {  // name 속성 추가
        log.debug("Toggle like for review: {}", reviewId);  // 디버깅용 로그

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
}