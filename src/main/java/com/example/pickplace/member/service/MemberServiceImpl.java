package com.example.pickplace.member.service;

import com.example.pickplace.member.controller.dto.JoinRequest;
import com.example.pickplace.member.controller.dto.LoginRequest;
import com.example.pickplace.member.repository.MemberRepository;
import com.example.pickplace.member.repository.entity.Member;
import com.example.pickplace.member.service.exception.DuplicateMemberException;
import com.example.pickplace.member.service.exception.InvalidPasswordException;
import com.example.pickplace.member.service.exception.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@Validated
@RequiredArgsConstructor
public class MemberServiceImpl  implements MemberService{

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    @Transactional
    public String join(JoinRequest joinRequest) {
        if (memberRepository.existsById(joinRequest.getId())) {
            throw new DuplicateMemberException("이미 존재하는 회원입니다.");
        }

        Member member = Member.builder()
                .id(joinRequest.getId())
                .password(passwordEncoder.encode(joinRequest.getPassword()))
                .name(joinRequest.getName())
                .phoneNumber(joinRequest.getPhoneNumber())
                .build();

        memberRepository.save(member);
        return "success";
    }

    @Override
    @Transactional(readOnly = true)
    public Member login(LoginRequest loginRequest) {
        Member member = memberRepository.findById(loginRequest.getId())
                .orElseThrow(() -> new MemberNotFoundException("존재하지 않는 회원입니다."));

        if (!passwordEncoder.matches(loginRequest.getPassword(), member.getPassword())) {
            throw new InvalidPasswordException("비밀번호가 일치하지 않습니다.");
        }

        return member;
    }
}
