package com.example.pickplace.member.controller.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewRequest {
    @NotBlank(message = "제목을 입력해주세요.")
    private String title;

// 지역 추가
//    @NotBlank(message = "지역을 입력해주세요.")
//    private String region;

    @NotBlank(message = "내용을 입력해주세요.")
    private String content;
}