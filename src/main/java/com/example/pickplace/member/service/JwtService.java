package com.example.pickplace.member.service;

import com.example.pickplace.member.repository.entity.Member;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

@Service
@Slf4j
public class JwtService {

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    public String extractUsername(String token) {
        log.info("JWT 토큰에서 사용자 이름 추출 시작.");
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        log.info("JWT 토큰에서 클레임 추출.");
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    public String generateToken(Member member) {
        log.info("사용자 ID '{}'에 대한 JWT 토큰 생성.", member.getId());
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", member.getId());
        claims.put("role", member.getRole().name());

        String token = Jwts.builder()
                .setClaims(claims)
                .setSubject(member.getId())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();

        log.info("생성된 JWT 토큰: {}", token);
        return token;
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        boolean valid = (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
        log.info("JWT 토큰 유효성 검사: {}", valid);
        return valid;
    }

    private boolean isTokenExpired(String token) {
        boolean expired = extractExpiration(token).before(new Date());
        log.info("JWT 토큰 만료 여부: {}", expired);
        return expired;
    }

    private Date extractExpiration(String token) {
        log.info("JWT 토큰에서 만료 날짜 추출.");
        return extractClaim(token, Claims::getExpiration);
    }

    private Claims extractAllClaims(String token) {
        log.info("JWT 토큰에서 모든 클레임 추출.");
        return Jwts.parserBuilder()
                .setSigningKey(getSignInKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private Key getSignInKey() {
        log.info("JWT 서명 키 가져오기.");
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public List<GrantedAuthority> extractAuthorities(String token) {
        log.info("JWT 토큰에서 권한 추출.");
        Claims claims = extractAllClaims(token);
        String role = claims.get("role", String.class);
        log.info("추출된 역할: {}", role);
        return List.of(new SimpleGrantedAuthority("ROLE_" + role));
    }
}
