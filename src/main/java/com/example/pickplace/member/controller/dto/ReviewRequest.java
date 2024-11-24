package com.example.pickplace.member.controller.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReviewRequest {
    @NotNull(message = "일정 번호를 입력해주세요.")
    private Long scheduleNo;  // 일정 번호 추가

    @NotBlank(message = "제목을 입력해주세요.")
    private String title;

    @NotBlank(message = "내용을 입력해주세요.")
    private String content;
}