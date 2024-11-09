package com.example.pickplace.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


// 회원가입(정보) 날짜 생성 및 업데이트 시간
@Configuration
@EnableJpaAuditing
public class JpaConfig {
}