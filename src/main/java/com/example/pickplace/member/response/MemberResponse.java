package com.example.pickplace.member.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MemberResponse {
    private String id;
    private String name;
    private String email;
    private String phoneNumber;
}
