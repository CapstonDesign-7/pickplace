package com.example.pickplace.member.repository;

import com.example.pickplace.member.repository.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

// DB와 통신
public interface MemberRepository extends JpaRepository<Member, Long> {
    boolean existsById(String id);
    Optional<Member> findById(String id);

    // 이름과 이메일로 사용자 찾기
    Optional<Member> findByNameAndEmail(String name, String email);

    // 이름, 아이디, 이메일로 사용자 찾기
    Optional<Member> findByNameAndIdAndEmail(String name, String id, String email);

}
