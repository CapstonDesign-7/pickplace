package com.example.pickplace.member.repository;

import com.example.pickplace.member.repository.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    List<Schedule> findByMemberId(String memberId);
    List<Schedule> findByRegion(String region);
    List<Schedule> findByStartDateBetween(LocalDate start, LocalDate end);
}