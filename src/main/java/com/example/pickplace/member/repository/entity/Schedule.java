package com.example.pickplace.member.repository.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Schedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_no")
    private Long id;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false)
    private String title;  // 여행 제목

    private String purpose;  // 여행 목적

    @Column(nullable = false)
    private Integer numberOfPeople;  // 인원 수

    @Column(nullable = false)
    private String region;  // 지역

    @Column(nullable = false)
    private LocalDate startDate;  // 출발 날짜

    @Column(nullable = false)
    private LocalDate endDate;  // 종료 날짜

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public boolean isWriter(String memberId) {
        return this.member.getId().equals(memberId);
    }

    // region 값 확인을 위한 메서드 추가
    public String getRegion() {
        if (region == null || region.trim().isEmpty()) {
            throw new IllegalStateException("Schedule region must not be null or empty");
        }
        return region;
    }

    public void update(String title, String purpose, Integer numberOfPeople, String region,
                       LocalDate startDate, LocalDate endDate) {
        this.title = title;
        this.purpose = purpose;
        this.numberOfPeople = numberOfPeople;
        this.region = region;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
