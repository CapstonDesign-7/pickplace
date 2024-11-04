package com.example.pickplace.member.controller;

import com.example.pickplace.member.controller.dto.JoinRequest;
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
        String result = memberService.join(joinRequest);
        return ResponseEntity.ok(new ApiResponse("회원가입이 완료되었습니다.", true));
    }

}

@Getter
@AllArgsConstructor
class ApiResponse {
    private String message;
    private boolean success;
}