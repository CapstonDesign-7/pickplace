package com.example.pickplace.member.controller;

import com.example.pickplace.member.controller.dto.JoinRequest;
import com.example.pickplace.member.controller.dto.LoginRequest;
import com.example.pickplace.member.repository.entity.Member;
import com.example.pickplace.member.service.MemberService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {
    private final MemberService memberService;

    @GetMapping("/hello")
    public String getHello(){
        return "Hello!!";
    }

    @PostMapping("/join")
    public ResponseEntity<ApiResponse> join(@RequestBody @Valid JoinRequest joinRequest) {
        String joinResult = memberService.join(joinRequest);
        return ResponseEntity.ok(new ApiResponse("회원가입이 완료되었습니다.", true));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody @Valid LoginRequest loginRequest) {
        Member member = memberService.login(loginRequest);
        // 세션 처리나 JWT 토큰 생성 등의 로직이 여기에 들어갈 수 있습니다.
        return ResponseEntity.ok(new ApiResponse("로그인이 완료되었습니다.", true));
    }

    @Getter
    @AllArgsConstructor
    private static class ApiResponse {
        private String message;
        private boolean success;
    }
}