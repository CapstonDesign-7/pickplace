package com.example.pickplace.security;

import com.example.pickplace.member.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");

        log.info("인증 권한 헤더: {}", authHeader);

        // Authorization 헤더가 없거나 "Bearer "로 시작하지 않으면 그냥 다음 필터로 넘김
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("Authorization 헤더가 없거나 Bearer로 시작하지 않음. 필터 체인 진행.");
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String jwt = authHeader.substring(7);
            log.info("JWT 토큰 추출: {}", jwt);

            final String userId = jwtService.extractUsername(jwt);
            log.info("JWT에서 추출한 사용자 ID: {}", userId);

            if (userId != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                log.info("사용자 인증 정보가 없으므로 인증 시도.");
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(userId);

                if (jwtService.isTokenValid(jwt, userDetails)) {
                    log.info("JWT 토큰이 유효함. 권한 부여 시작.");

                    List<GrantedAuthority> authorities = jwtService.extractAuthorities(jwt);
                    log.info("JWT에서 추출한 권한: {}", authorities);

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, authorities
                    );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                    // SecurityContext에 인증 정보를 설정
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.info("사용자 인증이 완료되어 SecurityContext에 저장됨.");
                } else {
                    log.warn("JWT 토큰이 유효하지 않음.");
                }
            }
        } catch (Exception e) {
            log.error("JWT 토큰 처리 중 오류 발생: {}", e.getMessage(), e);
        }

        filterChain.doFilter(request, response);
    }
}