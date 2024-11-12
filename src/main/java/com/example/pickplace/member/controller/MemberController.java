package com.example.pickplace.member.controller;

import com.example.pickplace.member.controller.dto.*;
import com.example.pickplace.member.repository.entity.Member;
import com.example.pickplace.member.response.ApiResponse;
import com.example.pickplace.member.response.LoginResponse;
import com.example.pickplace.member.response.MemberResponse;
import com.example.pickplace.member.service.EmailService;
import com.example.pickplace.member.service.JwtService;
import com.example.pickplace.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
@Slf4j
public class MemberController {

    private final MemberService memberService;
    private final JwtService jwtService;
    private final EmailService emailService;  // 이메일 서비스 추가

    // 회원가입 기능
    @PostMapping("/join")
    public ResponseEntity<ApiResponse> join(@RequestBody @Valid JoinRequest joinRequest) {
        log.info("회원가입 요청: {}", joinRequest.getId());
        String joinResult = memberService.join(joinRequest);
        log.info("회원가입 완료: {}", joinRequest.getId());
        return ResponseEntity.ok(new ApiResponse("회원가입이 완료되었습니다.", true));
    }

    // 로그인 기능
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
        log.info("로그인 요청: {}", loginRequest.getId());
        Member member = memberService.login(loginRequest);
        String token = jwtService.generateToken(member);
        log.info("로그인 완료: {}", loginRequest.getId());
        return ResponseEntity.ok(new LoginResponse(token, "로그인이 완료되었습니다.", true));
    }

    // 프로필 업데이트 기능
    @PostMapping("/update-profile")
    public ResponseEntity<ApiResponse> updateProfile(@RequestBody @Valid UpdateProfileRequest updateRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        memberService.updateProfile(userId, updateRequest);
        return ResponseEntity.ok(new ApiResponse("프로필이 성공적으로 업데이트되었습니다.", true));
    }

    // 비밀번호 변경 기능
    @PostMapping("/update-password")
    public ResponseEntity<ApiResponse> updatePassword(@RequestBody @Valid UpdatePasswordRequest passwordRequest) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        memberService.updatePassword(userId, passwordRequest);
        return ResponseEntity.ok(new ApiResponse("비밀번호가 성공적으로 변경되었습니다.", true));
    }

    // 인증된 사용자 정보(id, 회원 정보) 가져옴
    @GetMapping("/me")
    public ResponseEntity<MemberResponse> getMyInfo() {
        // SecurityContext에서 현재 인증된 사용자 정보를 가져옴
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = authentication.getName();

        // 사용자 정보 조회
        Member member = memberService.findById(userId);

        // 응답 데이터 생성
        MemberResponse response = new MemberResponse(
                member.getId(),
                member.getName(),
                member.getEmail(),
                member.getPhoneNumber()
        );

        return ResponseEntity.ok(response);
    }

    // 아이디 찾기
    @PostMapping("/find-id")
    public ResponseEntity<ApiResponse> findId(@RequestBody @Valid FindIdRequest findIdRequest) {
        log.info("아이디 찾기 요청: {}", findIdRequest);
        String userId = memberService.findIdByNameAndEmail(findIdRequest);

        // 이메일로 아이디 전송
        emailService.sendIdToEmail(findIdRequest.getEmail(), userId);

        log.info("아이디 전송 완료: {}", findIdRequest.getEmail());
        return ResponseEntity.ok(new ApiResponse("이메일로 아이디를 전송했습니다.", true));
    }

    // 비밀번호 찾기
    @PostMapping("/find-password")
    public ResponseEntity<ApiResponse> findPassword(@RequestBody @Valid FindPasswordRequest findPasswordRequest) {
        log.info("비밀번호 찾기 요청: {}", findPasswordRequest);
        String tempPassword = memberService.findPasswordByNameAndIdAndEmail(findPasswordRequest);

        // 이메일로 임시 비밀번호 전송
        emailService.sendTempPasswordToEmail(findPasswordRequest.getEmail(), tempPassword);

        log.info("임시 비밀번호 전송 완료: {}", findPasswordRequest.getEmail());
        return ResponseEntity.ok(new ApiResponse("이메일로 임시 비밀번호를 전송했습니다.", true));
    }
}