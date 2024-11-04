package com.example.pickplace.member.repository;

import com.example.pickplace.member.repository.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// DB와 통신
public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsById(String id);
    Optional<Member> findById(String id);
}
