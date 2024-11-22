package com.example.pickplace.member.controller;

import com.example.pickplace.member.controller.dto.ScheduleRequest;
import com.example.pickplace.member.response.ApiResponse;
import com.example.pickplace.member.response.ScheduleResponse;
import com.example.pickplace.member.service.ScheduleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/schedules")
@RequiredArgsConstructor
public class ScheduleController {

    private final ScheduleService scheduleService;

    // 일정 작성
    @PostMapping
    public ResponseEntity<ScheduleResponse> createSchedule(
            Authentication authentication,
            @Valid @RequestBody ScheduleRequest request) {
        String memberId = authentication.getName();
        return ResponseEntity.ok(scheduleService.createSchedule(memberId, request));
    }

    // 일정 업데이트
    @PutMapping("/{scheduleId}")
    public ResponseEntity<ScheduleResponse> updateSchedule(
            Authentication authentication,
            @PathVariable("scheduleId") Long scheduleId,
            @Valid @RequestBody ScheduleRequest request) {
        String memberId = authentication.getName();
        return ResponseEntity.ok(scheduleService.updateSchedule(memberId, scheduleId, request));
    }

    // 일정 삭제
    @DeleteMapping("/{scheduleId}")
    public ResponseEntity<ApiResponse> deleteSchedule(
            Authentication authentication,
            @PathVariable("scheduleId") Long scheduleId) {
        String memberId = authentication.getName();
        scheduleService.deleteSchedule(memberId, scheduleId);
        return ResponseEntity.ok(new ApiResponse("일정이 성공적으로 삭제되었습니다.", true));
    }

    // 일정 가져오기
    @GetMapping("/{scheduleId}")
    public ResponseEntity<ScheduleResponse> getSchedule(
            @PathVariable("scheduleId") Long scheduleId) {
        return ResponseEntity.ok(scheduleService.getSchedule(scheduleId));
    }

    // 내 일정 가져오기
    @GetMapping("/my")
    public ResponseEntity<List<ScheduleResponse>> getMySchedules(Authentication authentication) {
        String memberId = authentication.getName();
        return ResponseEntity.ok(scheduleService.getMySchedules(memberId));
    }

    // 지역별 일정 가져오기
    @GetMapping("/region/{region}")
    public ResponseEntity<List<ScheduleResponse>> getSchedulesByRegion(
            @PathVariable String region) {
        return ResponseEntity.ok(scheduleService.getSchedulesByRegion(region));
    }
}