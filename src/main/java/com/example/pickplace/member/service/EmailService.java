package com.example.pickplace.member.service;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    // 아이디를 이메일로 전송
    public void sendIdToEmail(String email, String userId) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("아이디 찾기 결과");
        message.setText("고객님의 아이디는: " + userId);

        mailSender.send(message);
    }

    // 임시 비밀번호를 이메일로 전송
    public void sendTempPasswordToEmail(String email, String tempPassword) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("비밀번호 찾기 결과");
        message.setText("고객님의 임시 비밀번호는: " + tempPassword);

        mailSender.send(message);
    }
}
