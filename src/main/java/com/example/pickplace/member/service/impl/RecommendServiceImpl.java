package com.example.pickplace.member.service.impl;

import com.example.pickplace.member.controller.dto.RecommendRequest;
import com.example.pickplace.member.repository.RecommendRepository;
import com.example.pickplace.member.repository.entity.Recommend;
import com.example.pickplace.member.response.RecommendResponse;
import com.example.pickplace.member.service.RecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RecommendServiceImpl implements RecommendService {
    private final RecommendRepository recommendRepository;

    @Override
    public RecommendResponse saveRecommend(RecommendRequest recommendRequest) {
        // TravelPlanRequest 데이터를 기반으로 TravelPlan 엔티티 생성
        Recommend recommend = new Recommend();
        recommend.setGroupSize(recommendRequest.getGroupSize());
        recommend.setRegion(recommendRequest.getRegion());
        recommend.setPurpose(recommendRequest.getPurpose());
        recommend.setDuration(recommendRequest.getDuration());

        // TravelPlan 엔티티를 저장
        Recommend savedRecommend = recommendRepository.save(recommend);

        // TravelPlanResponse DTO를 생성하여 반환
        return new RecommendResponse(
                savedRecommend.getId(),
                savedRecommend.getGroupSize(),
                savedRecommend.getRegion(),
                savedRecommend.getPurpose(),
                savedRecommend.getDuration()
        );
    }
}