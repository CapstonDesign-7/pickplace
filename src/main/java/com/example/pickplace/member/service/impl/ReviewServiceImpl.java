package com.example.pickplace.member.service.impl;

import com.example.pickplace.member.controller.dto.ReviewRequest;
import com.example.pickplace.member.repository.LikeRepository;
import com.example.pickplace.member.repository.MemberRepository;
import com.example.pickplace.member.repository.ReviewRepository;
import com.example.pickplace.member.repository.ScheduleRepository;
import com.example.pickplace.member.repository.entity.*;
import com.example.pickplace.member.response.ReviewResponse;
import com.example.pickplace.member.service.ImageUploadService;
import com.example.pickplace.member.service.ReviewService;
import com.example.pickplace.member.service.exception.MemberNotFoundException;
import com.example.pickplace.member.service.exception.ReviewNotFoundException;
import com.example.pickplace.member.service.exception.ScheduleNotFoundException;
import com.example.pickplace.member.service.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final ScheduleRepository scheduleRepository;
    private final ImageUploadService imageUploadService;
    private final LikeRepository likeRepository;

    // 리뷰 작성
    @Override
    @Transactional
    public ReviewResponse createReview(String memberId, ReviewRequest request, List<MultipartFile> images) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("회원을 찾을 수 없습니다."));

        Schedule schedule = scheduleRepository.findById(request.getScheduleNo())
                .orElseThrow(() -> new ScheduleNotFoundException("일정을 찾을 수 없습니다."));

        // 해당 일정의 작성자인지 확인
        if (!schedule.isWriter(memberId)) {
            throw new UnauthorizedException("본인의 일정에 대해서만 리뷰를 작성할 수 있습니다.");
        }

        Review review = Review.builder()
                .title(request.getTitle().trim())
                .content(request.getContent().trim())
                .member(member)
                .schedule(schedule)
                .region(schedule.getRegion()) // region 명시적 설정
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

    // 리뷰 업데이트
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
                .collect(Collectors.toList());

        // 기존 이미지 모두 삭제
        review.getImages().clear();

        // 새로운 이미지가 있는 경우에만 기존 이미지 파일 삭제 및 새 이미지 업로드
        if (images != null && !images.isEmpty()) {
            // 기존 이미지 파일 삭제
            oldImageUrls.forEach(imageUploadService::deleteImage);

            // 새로운 이미지 업로드 및 추가
            List<String> newImageUrls = imageUploadService.uploadImages(images);
            for (String imageUrl : newImageUrls) {
                ReviewImage reviewImage = ReviewImage.builder()
                        .imageUrl(imageUrl)
                        .review(review)
                        .build();
                review.addImage(reviewImage);
            }
        }

        // 제목과 내용 업데이트
        review.update(request.getTitle(), request.getContent());

        return ReviewResponse.from(review, memberId);
    }

    // 리뷰 수정을 위한 불러오기
    @Override
    public ReviewResponse getReviewForEdit(String memberId, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("리뷰를 찾을 수 없습니다."));

        if (!review.isWriter(memberId)) {
            throw new UnauthorizedException("리뷰를 수정할 권한이 없습니다.");
        }

        return ReviewResponse.from(review, memberId);
    }

    // 리뷰 삭제
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
                .collect(Collectors.toList());

        // 리뷰 삭제
        reviewRepository.delete(review);

        // 실제 이미지 파일 삭제
        imageUrls.forEach(imageUploadService::deleteImage);
    }

    // 좋아요 수 카운팅
    @Override
    public int getLikeCount(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("리뷰를 찾을 수 없습니다."));
        return review.getLikeCount();
    }

    // 특정 하나의 리뷰 가져오기
    @Override
    public ReviewResponse getReview(Long reviewId, String currentUserId) {  // 매개변수 추가
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ReviewNotFoundException("리뷰를 찾을 수 없습니다."));
        return ReviewResponse.from(review, currentUserId);
    }

    // 내 리뷰 가져오기
    @Override
    public List<ReviewResponse> getMyReviews(String memberId) {
        return reviewRepository.findByMemberId(memberId).stream()
                .map(review -> ReviewResponse.from(review, memberId))  // memberId 전달
                .collect(Collectors.toList());
    }

    // 전체 리뷰 가져오기(최신순)
    @Override
    public List<ReviewResponse> getAllReviews(String currentUserId) {
        List<Review> reviews = reviewRepository.findAllOrderByCreatedAtDesc();
        return reviews.stream()
                .map(review -> ReviewResponse.from(review, currentUserId))
                .collect(Collectors.toList());
    }

    // 추천순 정렬
    @Override
    public List<ReviewResponse> getReviewsSortedByLikes(String currentUserId) {
        List<Review> reviews = reviewRepository.findAllOrderByLikesCountDesc();
        return reviews.stream()
                .map(review -> ReviewResponse.from(review, currentUserId))
                .collect(Collectors.toList());
    }

    // 리뷰 검색 기능
    @Override
    public List<ReviewResponse> searchReviewsByRegion(String region, String currentUserId) {
        List<Review> reviews = reviewRepository.findByRegionContaining(region);
        return reviews.stream()
                .map(review -> ReviewResponse.from(review, currentUserId))
                .collect(Collectors.toList());
    }

    // 좋아요 토글 기능(켜짐/꺼짐)
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

    // 사용자가 좋아요 눌렀는지 확인
    @Override
    public boolean isLikedByMember(String memberId, Long reviewId) {
        return likeRepository.existsByMemberIdAndReviewId(memberId, reviewId);
    }

}