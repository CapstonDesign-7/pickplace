package com.example.pickplace.member.response;

import com.example.pickplace.member.repository.entity.Schedule;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class ScheduleResponse {
    private Long id;
    private String title;
    private String purpose;
    private Integer numberOfPeople;
    private String region;
    private LocalDate startDate;
    private LocalDate endDate;
    private String writerName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ScheduleResponse from(Schedule schedule) {
        return ScheduleResponse.builder()
                .id(schedule.getId())
                .title(schedule.getTitle())
                .purpose(schedule.getPurpose())
                .numberOfPeople(schedule.getNumberOfPeople())
                .region(schedule.getRegion())
                .startDate(schedule.getStartDate())
                .endDate(schedule.getEndDate())
                .writerName(schedule.getMember().getName())
                .createdAt(schedule.getCreatedAt())
                .updatedAt(schedule.getUpdatedAt())
                .build();
    }
}
