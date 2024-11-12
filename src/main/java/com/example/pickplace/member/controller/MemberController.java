package com.example.pickplace.member.controller;

import com.example.pickplace.member.controller.dto.JoinRequest;
import com.example.pickplace.member.controller.dto.LoginRequest;
import com.example.pickplace.member.controller.dto.UpdatePasswordRequest;
import com.example.pickplace.member.controller.dto.UpdateProfileRequest;
import com.example.pickplace.member.repository.entity.Member;
import com.example.pickplace.member.response.ApiResponse;
import com.example.pickplace.member.response.LoginResponse;
import com.example.pickplace.member.response.MemberResponse;
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
}