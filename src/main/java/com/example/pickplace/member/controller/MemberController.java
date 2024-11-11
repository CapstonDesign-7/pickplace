package com.example.pickplace.member.controller;

import com.example.pickplace.member.controller.dto.JoinRequest;
import com.example.pickplace.member.controller.dto.LoginRequest;
import com.example.pickplace.member.repository.entity.Member;
import com.example.pickplace.member.response.LoginResponse;
import com.example.pickplace.member.service.JwtService;
import com.example.pickplace.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
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

    @PostMapping("/join")
    public ResponseEntity<ApiResponse> join(@RequestBody @Valid JoinRequest joinRequest) {
        log.info("회원가입 요청: {}", joinRequest.getId());
        String joinResult = memberService.join(joinRequest);
        log.info("회원가입 완료: {}", joinRequest.getId());
        return ResponseEntity.ok(new ApiResponse("회원가입이 완료되었습니다.", true));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
        log.info("로그인 요청: {}", loginRequest.getId());
        Member member = memberService.login(loginRequest);
        String token = jwtService.generateToken(member);
        log.info("로그인 완료: {}", loginRequest.getId());
        return ResponseEntity.ok(new LoginResponse(token, "로그인이 완료되었습니다.", true));
    }

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
                member.getRole().name(),
                member.getName()
        );

        return ResponseEntity.ok(response);
    }


    @Getter
    @AllArgsConstructor
    private static class ApiResponse {
        private String message;
        private boolean success;
    }
    // 응답 DTO
    @Getter
    @AllArgsConstructor
    private static class MemberResponse {
        private String id;
        private String role;
        private String name;
    }
}