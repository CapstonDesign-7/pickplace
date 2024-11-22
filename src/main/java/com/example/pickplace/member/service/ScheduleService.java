package com.example.pickplace.member.service;

import com.example.pickplace.member.controller.dto.ScheduleRequest;
import com.example.pickplace.member.response.ScheduleResponse;

import java.util.List;

public interface ScheduleService {
    ScheduleResponse createSchedule(String memberId, ScheduleRequest request);
    ScheduleResponse updateSchedule(String memberId, Long scheduleId, ScheduleRequest request);
    void deleteSchedule(String memberId, Long scheduleId);
    ScheduleResponse getSchedule(Long scheduleId);
    List<ScheduleResponse> getMySchedules(String memberId);
    List<ScheduleResponse> getSchedulesByRegion(String region);
}
