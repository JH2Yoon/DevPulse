package com.jhy.devpulse.common.security;

import java.util.*;

import javax.crypto.SecretKey;

import com.jhy.devpulse.common.exception.CustomException;
import com.jhy.devpulse.common.exception.ErrorCode;
import com.jhy.devpulse.domain.member.entity.Role;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class JwtProvider {

    static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String AUTHORIZATION_KEY = "role";

    @Value("${jwt.secret-key}")
    private String secretKey;

    @Value("${jwt.access-token-expiration}")
    private long accessTokenExpiration;

    @Value("${jwt.refresh-token-expiration}")
    private long refreshTokenExpiration;

    private SecretKey key;

    @PostConstruct
    public void init() {
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    /**
     * Access Token 생성
     */
    public String createAccessToken(Long memberId, Role role) {
        return createToken(memberId, role, accessTokenExpiration);
    }

    /**
     * Refresh Token 생성
     */
    public String createRefreshToken(Long memberId) {
        return createToken(memberId, null, refreshTokenExpiration);
    }

    /**
     * Token 생성
     */
    private String createToken(Long memberId, Role role, long expiration) {

        Date now = new Date();

        JwtBuilder builder = Jwts.builder()
                .subject(String.valueOf(memberId))
                .issuedAt(now)
                .expiration(new Date(now.getTime() + expiration));

        if (role != null) {
            builder.claim(AUTHORIZATION_KEY, role.name());
        }

        return builder
                .signWith(key)
                .compact();
    }

    /**
     * Bearer Token 추출
     */
    public String resolveToken(String bearerToken) {

        if (StringUtils.hasText(bearerToken)
                && bearerToken.startsWith(BEARER_PREFIX)) {

            return bearerToken.substring(BEARER_PREFIX.length());
        }

        return null;
    }

    /**
     * Claims 조회
     */
    public Claims parseClaims(String token) {

        try {

            return Jwts.parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

        } catch (ExpiredJwtException e) {
            throw new CustomException(ErrorCode.EXPIRED_TOKEN);

        } catch (JwtException | IllegalArgumentException e) {
            throw new CustomException(ErrorCode.INVALID_TOKEN);
        }
    }

    /**
     * Token 유효성 검증
     */
    public boolean validateToken(String token) {

        try {
            parseClaims(token);
            return true;
        } catch (CustomException e) {
            return false;
        }
    }

    /**
     * Authentication 생성
     */
    public Authentication getAuthentication(String token) {

        Claims claims = parseClaims(token);

        Long memberId = Long.valueOf(claims.getSubject());

        String role = claims.get(AUTHORIZATION_KEY, String.class);

        return new UsernamePasswordAuthenticationToken(
                memberId,
                null,
                Collections.singleton(new SimpleGrantedAuthority("ROLE_" + role)));
    }

    /**
     * Member ID 조회
     */
    public Long getMemberId(String token) {
        return Long.valueOf(parseClaims(token).getSubject());
    }
}
