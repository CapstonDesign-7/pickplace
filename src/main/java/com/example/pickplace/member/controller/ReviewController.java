package com.example.pickplace.member.controller;

import com.example.pickplace.member.controller.dto.ReviewRequest;
import com.example.pickplace.member.response.ReviewResponse;
import com.example.pickplace.member.service.ReviewService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
@RequiredArgsConstructor
public class ReviewController {

    private final ReviewService reviewService;
    private final ObjectMapper objectMapper;  // Jackson ObjectMapper 주입

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

    @GetMapping("/{reviewId}")
    public ResponseEntity<ReviewResponse> getReview(@PathVariable Long reviewId) {
        return ResponseEntity.ok(reviewService.getReview(reviewId));
    }

    @GetMapping("/my")
    public ResponseEntity<List<ReviewResponse>> getMyReviews(Authentication authentication) {
        String memberId = authentication.getName();
        return ResponseEntity.ok(reviewService.getMyReviews(memberId));
    }

    @GetMapping
    public ResponseEntity<List<ReviewResponse>> getAllReviews() {
        return ResponseEntity.ok(reviewService.getAllReviews());
    }
}