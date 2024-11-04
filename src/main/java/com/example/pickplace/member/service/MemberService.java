package com.example.pickplace.member.service;

import com.example.pickplace.member.controller.dto.JoinRequest;

// 컨트롤러->서비스->레포지토리
public interface MemberService {

    String join(JoinRequest joinRequest);
}
