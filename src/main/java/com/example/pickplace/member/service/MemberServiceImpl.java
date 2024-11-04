package com.example.pickplace.member.service;

import com.example.pickplace.member.controller.dto.JoinRequest;
import com.example.pickplace.member.repository.MemberRepository;
import com.example.pickplace.member.repository.entity.Member;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberServiceImpl  implements MemberService{
    private final MemberRepository memberRepository;

    @Override
    @Transactional
    public String join(JoinRequest joinRequest) {
        // 중복 회원 검증
        if (memberRepository.existsById(joinRequest.getId())){
            throw new IllegalStateException("이미 존재하는 회원입니다.");
        }

        Member member = Member.builder()
                .id(joinRequest.getId())
                .name(joinRequest.getName())
                .phoneNumber(joinRequest.getPhoneNumber())
                .build();

        memberRepository.save(member);
        return "success";
    }
}
