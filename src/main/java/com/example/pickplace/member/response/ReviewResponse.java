package com.example.pickplace.member.response;

import com.example.pickplace.member.repository.entity.Review;
import com.example.pickplace.member.repository.entity.ReviewImage;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
public class ReviewResponse {
    private Long id;
    private String title;
    private String content;
    private String writerName;
    private List<String> imageUrls;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private int likeCount;      // 좋아요 기능
    private boolean isLiked;

    public static ReviewResponse from(Review review, String currentUserId) {
        return ReviewResponse.builder()
                .id(review.getId())
                .title(review.getTitle())
                .content(review.getContent())
                .writerName(review.getMember().getName())
                .imageUrls(review.getImages().stream()
                        .map(ReviewImage::getImageUrl)
                        .toList())
                .createdAt(review.getCreatedAt())
                .updatedAt(review.getUpdatedAt())
                .likeCount(review.getLikeCount())
                .isLiked(currentUserId != null && review.isLikedBy(currentUserId))
                .build();
    }
}