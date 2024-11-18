package com.example.pickplace.member.service.impl;

import com.example.pickplace.member.controller.dto.ReviewRequest;
import com.example.pickplace.member.repository.LikeRepository;
import com.example.pickplace.member.repository.MemberRepository;
import com.example.pickplace.member.repository.ReviewRepository;
import com.example.pickplace.member.repository.entity.Like;
import com.example.pickplace.member.repository.entity.Member;
import com.example.pickplace.member.repository.entity.Review;
import com.example.pickplace.member.repository.entity.ReviewImage;
import com.example.pickplace.member.response.ReviewResponse;
import com.example.pickplace.member.service.ImageUploadService;
import com.example.pickplace.member.service.ReviewService;
import com.example.pickplace.member.service.exception.MemberNotFoundException;
import com.example.pickplace.member.service.exception.ReviewNotFoundException;
import com.example.pickplace.member.service.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final ImageUploadService imageUploadService;
    private final LikeRepository likeRepository;

    @Override
    @Transactional
    public ReviewResponse createReview(String memberId, ReviewRequest request, List<MultipartFile> images) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("회원을 찾을 수 없습니다."));

        Review review = Review.builder()
                .title(request.getTitle().trim())
                .content(request.getContent().trim())
                .member(member)
                .images(new ArrayList<>())
                .build();

        if (images != null && !images.isEmpty()) {
            List<String> imageUrls = imageUploadService.uploadImages(images);
            for (String imageUrl : imageUrls) {
                ReviewImage reviewImage = ReviewImage.builder()
                        .imageUrl(imageUrl)
                        .review(review)
                        .build();
                review.addImage(reviewImage);
            }
        }

        Review savedReview = reviewRepository.save(review);
        return ReviewResponse.from(savedReview, memberId);
    }

    @Override
    @Transactional
    public ReviewResponse updateReview(String memberId, Long reviewId, ReviewRequest request, List<MultipartFile> images) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("리뷰를 찾을 수 없습니다."));

        if (!review.isWriter(memberId)) {
            throw new UnauthorizedException("리뷰를 수정할 권한이 없습니다.");
        }

        // 기존 이미지 URL 저장
        List<String> oldImageUrls = review.getImages().stream()
                .map(ReviewImage::getImageUrl)
                .toList();

        // 기존 이미지 모두 삭제
        review.getImages().clear();

        // 새로운 이미지 업로드 및 추가
        if (images != null && !images.isEmpty()) {
            // 기존 이미지 파일 삭제
            oldImageUrls.forEach(imageUploadService::deleteImage);

            // 새로운 이미지 업로드 및 추가
            List<String> newImageUrls = imageUploadService.uploadImages(images);
            for (String imageUrl : newImageUrls) {
                ReviewImage reviewImage = ReviewImage.builder()
                        .imageUrl(imageUrl)
                        .build();
                review.addImage(reviewImage);
            }
        }

        review.update(request.getTitle(), request.getContent());
        return ReviewResponse.from(review, memberId);
    }

    @Override
    @Transactional
    public void deleteReview(String memberId, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("리뷰를 찾을 수 없습니다."));

        if (!review.isWriter(memberId)) {
            throw new UnauthorizedException("리뷰를 삭제할 권한이 없습니다.");
        }

        // 이미지 URL 목록 저장
        List<String> imageUrls = review.getImages().stream()
                .map(ReviewImage::getImageUrl)
                .toList();

        // 리뷰 삭제
        reviewRepository.delete(review);

        // 실제 이미지 파일 삭제
        imageUrls.forEach(imageUploadService::deleteImage);
    }

    @Override
    public int getLikeCount(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("리뷰를 찾을 수 없습니다."));
        return review.getLikeCount();
    }

    @Override
    public ReviewResponse getReview(Long reviewId, String currentUserId) {  // 매개변수 추가
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("리뷰를 찾을 수 없습니다."));
        return ReviewResponse.from(review, currentUserId);
    }

    @Override
    public List<ReviewResponse> getMyReviews(String memberId) {
        return reviewRepository.findByMemberId(memberId).stream()
                .map(review -> ReviewResponse.from(review, memberId))  // memberId 전달
                .collect(Collectors.toList());
    }

    // 리뷰 목록 조회
    @Override
    public List<ReviewResponse> getAllReviews(String currentUserId) {
        List<Review> reviews = reviewRepository.findAllOrderByLikesCountAndCreatedAtDesc();
        return reviews.stream()
                .map(review -> ReviewResponse.from(review, currentUserId))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void toggleLike(String memberId, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("리뷰를 찾을 수 없습니다."));
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("회원을 찾을 수 없습니다."));

        Optional<Like> existingLike = likeRepository.findByMemberIdAndReviewId(memberId, reviewId);

        if (existingLike.isPresent()) {
            // 이미 좋아요가 있으면 제거
            likeRepository.delete(existingLike.get());
        } else {
            // 좋아요가 없으면 추가
            Like like = Like.builder()
                    .member(member)
                    .review(review)
                    .build();
            likeRepository.save(like);
        }
    }

    @Override
    public boolean isLikedByMember(String memberId, Long reviewId) {
        return likeRepository.existsByMemberIdAndReviewId(memberId, reviewId);
    }
}