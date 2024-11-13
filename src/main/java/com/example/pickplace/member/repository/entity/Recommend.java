package com.example.pickplace.member.repository.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Builder
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)

public class Recommend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userId; // 사용자 ID, JWT에서 가져온 ID로 설정
    private Integer groupSize;
    private String region;
    private String purpose;
    private String duration;

}
