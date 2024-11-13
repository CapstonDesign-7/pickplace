package com.example.pickplace.member.controller.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RecommendRequest {

    @NotNull(message = "인원수는 필수 입력값입니다.")
    private Integer groupSize;

    @NotBlank(message = "선호하는 지역은 필수 입력값입니다.")
    private String region;

    @NotBlank(message = "여행 목적은 필수 입력값입니다.")
    private String purpose;

    @NotBlank(message = "여행 기간은 필수 입력값입니다.")
    private String duration;
}
