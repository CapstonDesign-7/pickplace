package com.example.pickplace.member.controller;

import com.example.pickplace.member.controller.dto.RecommendRequest;
import com.example.pickplace.member.response.RecommendResponse;
import com.example.pickplace.member.service.RecommendService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/travel-plan")
@RequiredArgsConstructor
public class RecommendController {

    private final RecommendService recommendService;

    @PostMapping("/recommend")
    public ResponseEntity<RecommendResponse> recommend(@RequestBody @Valid RecommendRequest recommendRequest) {
        // 여행 계획 저장
        RecommendResponse response = recommendService.saveRecommend(recommendRequest);

        return ResponseEntity.ok(response);
    }
}