package com.example.pickplace.member.repository.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Builder.Default  // Builder 패턴 사용 시 빈 ArrayList로 초기화
    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ReviewImage> images = new ArrayList<>();

    @Builder.Default
    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Like> likes = new ArrayList<>();  // null이 아닌 빈 리스트로 초기화

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // 이미지 추가를 위한 편의 메서드
    public void addImage(ReviewImage image) {
        this.images.add(image);
        image.setReview(this);
    }

    // 좋아요 수를 반환하는 메서드 추가
    public int getLikeCount() {
        return likes != null ? likes.size() : 0;  // null 체크 추가
    }

    // 특정 사용자가 좋아요를 눌렀는지 확인하는 메서드
    public boolean isLikedBy(String memberId) {
        return likes.stream()
                .anyMatch(like -> like.getMember().getId().equals(memberId));
    }

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }

    public boolean isWriter(String memberId) {
        return this.member.getId().equals(memberId);
    }
}