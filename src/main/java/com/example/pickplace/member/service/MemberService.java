package com.example.pickplace.member.service;

import com.example.pickplace.member.controller.dto.*;
import com.example.pickplace.member.repository.entity.Member;

// 컨트롤러->서비스->레포지토리
public interface MemberService {

    String join(JoinRequest joinRequest);
    Member login(LoginRequest loginRequest);
    Member findById(String id);

    void updateProfile(String userId, UpdateProfileRequest updateRequest);
    void updatePassword(String userId, UpdatePasswordRequest passwordRequest);

    String findIdByNameAndEmail(FindIdRequest request); // 아이디 찾기
    String findPasswordByNameAndIdAndEmail(FindPasswordRequest request); // 비밀번호 찾기
}
