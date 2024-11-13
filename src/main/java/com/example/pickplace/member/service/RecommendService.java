package com.example.pickplace.member.service;

import com.example.pickplace.member.controller.dto.RecommendRequest;
import com.example.pickplace.member.response.RecommendResponse;

public interface RecommendService {
    RecommendResponse saveRecommend(RecommendRequest recommendRequest);
}
