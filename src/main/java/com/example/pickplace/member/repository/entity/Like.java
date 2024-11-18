package com.example.pickplace.member.repository.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "review_likes",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"member_id", "review_id"})
        })
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Like {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idx;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;
}
