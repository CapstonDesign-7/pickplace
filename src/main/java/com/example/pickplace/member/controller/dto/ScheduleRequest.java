package com.example.pickplace.member.controller.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ScheduleRequest {
    @NotBlank(message = "제목을 입력해주세요.")
    private String title;

    @NotNull(message = "인원 수를 입력해주세요.")
    @Min(value = 1, message = "인원 수는 1명 이상이어야 합니다.")
    private Integer numberOfPeople;

    @NotBlank(message = "지역을 선택해주세요.")
    private String region;

    private String purpose;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "출발 날짜를 입력해주세요.")
    private LocalDate startDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    @NotNull(message = "종료 날짜를 입력해주세요.")
    private LocalDate endDate;
}