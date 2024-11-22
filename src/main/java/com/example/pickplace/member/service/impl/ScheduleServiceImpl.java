package com.example.pickplace.member.service.impl;

import com.example.pickplace.member.controller.dto.ScheduleRequest;
import com.example.pickplace.member.repository.MemberRepository;
import com.example.pickplace.member.repository.ScheduleRepository;
import com.example.pickplace.member.repository.entity.Member;
import com.example.pickplace.member.repository.entity.Schedule;
import com.example.pickplace.member.response.ScheduleResponse;
import com.example.pickplace.member.service.ScheduleService;
import com.example.pickplace.member.service.exception.MemberNotFoundException;
import com.example.pickplace.member.service.exception.ScheduleNotFoundException;
import com.example.pickplace.member.service.exception.UnauthorizedException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ScheduleServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public ScheduleResponse createSchedule(String memberId, ScheduleRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberNotFoundException("회원을 찾을 수 없습니다."));

        Schedule schedule = Schedule.builder()
                .member(member)
                .title(request.getTitle())
                .purpose(request.getPurpose())
                .numberOfPeople(request.getNumberOfPeople())
                .region(request.getRegion())
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .build();

        return ScheduleResponse.from(scheduleRepository.save(schedule));
    }

    @Override
    @Transactional
    public ScheduleResponse updateSchedule(String memberId, Long scheduleId, ScheduleRequest request) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleNotFoundException("일정을 찾을 수 없습니다."));

        if (!schedule.isWriter(memberId)) {
            throw new UnauthorizedException("일정을 수정할 권한이 없습니다.");
        }

        schedule.update(request.getTitle(), request.getPurpose(), request.getNumberOfPeople(),
                request.getRegion(), request.getStartDate(), request.getEndDate());

        return ScheduleResponse.from(schedule);
    }

    @Override
    @Transactional
    public void deleteSchedule(String memberId, Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleNotFoundException("일정을 찾을 수 없습니다."));

        if (!schedule.isWriter(memberId)) {
            throw new UnauthorizedException("일정을 삭제할 권한이 없습니다.");
        }

        scheduleRepository.delete(schedule);
    }

    @Override
    public ScheduleResponse getSchedule(Long scheduleId) {
        Schedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ScheduleNotFoundException("일정을 찾을 수 없습니다."));
        return ScheduleResponse.from(schedule);
    }

    @Override
    public List<ScheduleResponse> getMySchedules(String memberId) {
        return scheduleRepository.findByMemberId(memberId).stream()
                .map(ScheduleResponse::from)
                .collect(Collectors.toList());
    }

    @Override
    public List<ScheduleResponse> getSchedulesByRegion(String region) {
        return scheduleRepository.findByRegion(region).stream()
                .map(ScheduleResponse::from)
                .collect(Collectors.toList());
    }
}