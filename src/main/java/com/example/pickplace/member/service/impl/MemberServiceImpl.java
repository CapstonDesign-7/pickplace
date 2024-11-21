package com.example.pickplace.member.service.impl;

import com.example.pickplace.member.controller.dto.*;
import com.example.pickplace.member.repository.MemberRepository;
import com.example.pickplace.member.repository.entity.Member;
import com.example.pickplace.member.repository.entity.Role;
import com.example.pickplace.member.service.MemberService;
import com.example.pickplace.member.service.exception.DuplicateMemberException;
import com.example.pickplace.member.service.exception.InvalidPasswordException;
import com.example.pickplace.member.service.exception.MemberNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.UUID;

@Service
@Validated
@RequiredArgsConstructor
public class MemberServiceImpl  implements MemberService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원 가입
    @Override
    @Transactional
    public void join(JoinRequest joinRequest) {
        if (memberRepository.existsById(joinRequest.getId())) {
            throw new DuplicateMemberException("이미 존재하는 아이디입니다.");
        }
        if (memberRepository.existsByEmail(joinRequest.getEmail())) {
            throw new DuplicateMemberException("이미 존재하는 이메일입니다.");
        }

        Member member = Member.builder()
                .id(joinRequest.getId())
                .password(passwordEncoder.encode(joinRequest.getPassword()))
                .name(joinRequest.getName())
                .phoneNumber(joinRequest.getPhoneNumber())
                .email(joinRequest.getEmail())
                .gender(joinRequest.getGender())
                .birth(joinRequest.getBirth())
                .role(Role.USER)
                .build();

        memberRepository.save(member);
    }

    // 로그인
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

    // id 존재 확인
    @Override
    @Transactional(readOnly = true)
    public Member findById(String id) {
        return memberRepository.findById(id)
                .orElseThrow(() -> new MemberNotFoundException("존재하지 않는 회원입니다."));
    }

    // 프로필 업데이트
    @Transactional
    public void updateProfile(String userId, UpdateProfileRequest updateRequest) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new MemberNotFoundException("존재하지 않는 회원입니다."));

        member.updateProfile(updateRequest.getName(), updateRequest.getPhoneNumber(),
                updateRequest.getEmail());
    }

    // 비밀번호 업데이트
    @Transactional
    public void updatePassword(String userId, UpdatePasswordRequest passwordRequest) {
        Member member = memberRepository.findById(userId)
                .orElseThrow(() -> new MemberNotFoundException("존재하지 않는 회원입니다."));

        if (!passwordEncoder.matches(passwordRequest.getCurrentPassword(), member.getPassword())) {
            throw new InvalidPasswordException("현재 비밀번호가 일치하지 않습니다.");
        }

        member.setPassword(passwordEncoder.encode(passwordRequest.getNewPassword()));
    }

    // 아이디 찾기 (이름과 이메일로 아이디 조회)
    @Override
    public String findIdByNameAndEmail(FindIdRequest request) {
        memberRepository.findByNameAndEmail(request.getName(), request.getEmail())
                .orElseThrow(() -> new MemberNotFoundException("이름과 이메일에 해당하는 회원이 존재하지 않습니다."));

        return "아이디가 이메일로 전송되었습니다.";
    }

    // 비밀번호 찾기 (아이디와 이메일로 임시 비밀번호 발급)
    @Override
    public String findPasswordByNameAndIdAndEmail(FindPasswordRequest request) {
        Member member = memberRepository.findByNameAndIdAndEmail(request.getName(), request.getId(), request.getEmail())
                .orElseThrow(() -> new MemberNotFoundException("해당하는 회원이 존재하지 않습니다."));

        // 임시 비밀번호 생성 (UUID 사용)
        String temporaryPassword = UUID.randomUUID().toString();

        // 임시 비밀번호를 암호화하여 저장
        member.setPassword(passwordEncoder.encode(temporaryPassword));
        memberRepository.save(member);

        return temporaryPassword;
    }

}
