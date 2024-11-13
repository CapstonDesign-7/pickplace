package com.example.pickplace.member.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RecommendResponse {

    private Long id;
    private Integer groupSize;
    private String region;
    private String purpose;
    private String duration;
}
