package com.haru.LogMe.global.jwt;

import com.haru.LogMe.global.exception.CustomException;
import com.haru.LogMe.global.exception.ErrorCode;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.stream.Collectors;

@Slf4j
@Component
public class JwtTokenProvider {
    private final Key key;
    private final long accessTokenValidityInMilliseconds;
    private final UserDetailsService userDetailsService;

    private static final String AUTHORITIES_KEY = "auth";

    public JwtTokenProvider(
            @Value("${jwt.secret}") String secretKey,
            @Value("${jwt.access-token-expiration-seconds}") long accessTokenValidityInSeconds,
            UserDetailsService userDetailsService) {

        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenValidityInMilliseconds = accessTokenValidityInSeconds * 1000;
        this.userDetailsService = userDetailsService;
    }

    /**
     * Authentication 객체를 받아 Access Token을 생성합니다. -> 로그인 필터용: 소셜로그인 로그인 시 토큰 발급.
     */
    public String generateAccessToken(Authentication authentication) {
        String authorities = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        Date validity = new Date(now + this.accessTokenValidityInMilliseconds);

        // Principal에서 username(우리의 경우 ID)을 가져옴
        String subject;
        if (authentication.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            subject = userDetails.getUsername(); // CustomUserDetailsService가 ID를 리턴
        } else {
            subject = authentication.getName();
        }

        return Jwts.builder()
                .setSubject(subject)
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(key, SignatureAlgorithm.HS512)
                .setExpiration(validity)
                .compact();
    }

    /**
     * user 엔티티(UserDetails)를 받아 access token 생성.
     */
    public String generateAccessToken(com.haru.LogMe.domain.user.entity.User user) {
        String authorities = user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        long now = (new Date()).getTime();
        Date validity = new Date(now + this.accessTokenValidityInMilliseconds);

        return Jwts.builder()
                .setSubject(String.valueOf(user.getUserId()))
                .claim(AUTHORITIES_KEY, authorities)
                .signWith(key, SignatureAlgorithm.HS512) // 기존 방식과 동일하게 HS512
                .setExpiration(validity)
                .compact();
    }


    /**
     * 토큰에서 인증 정보(Authentication)를 추출합니다.
     */
    public Authentication getAuthentication(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();

        Collection<? extends GrantedAuthority> authorities =
                Arrays.stream(claims.get(AUTHORITIES_KEY).toString().split(","))
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toList());

        // claims.getSubject()에는 토큰 생성 시 넣은 "userId"가 들어있음.
        // userDetailsService.loadUserByUsername()가 CustomUserDetailsService의 메서드를 호출.
        // 반환되는 userDetails는 DB에서 조회한 'com.haru.LogMe.domain.user.entity.User' 객체임.
        UserDetails userDetails = userDetailsService.loadUserByUsername(claims.getSubject());

        return new UsernamePasswordAuthenticationToken(userDetails, token, userDetails.getAuthorities());
    }

    /**
     * 토큰의 유효성을 검증합니다.
     * 실패 시 CustomException(ErrorCode.INVALID_ACCESSTOKEN)을 던집니다.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            log.warn("잘못된 JWT 서명입니다.");
            throw new CustomException(ErrorCode.INVALID_ACCESSTOKEN);
        } catch (ExpiredJwtException e) {
            log.warn("만료된 JWT 토큰입니다.");
            throw new CustomException(ErrorCode.INVALID_ACCESSTOKEN);
        } catch (UnsupportedJwtException e) {
            log.warn("지원되지 않는 JWT 토큰입니다.");
            throw new CustomException(ErrorCode.INVALID_ACCESSTOKEN);
        } catch (IllegalArgumentException e) {
            log.warn("JWT 토큰이 잘못되었습니다.");
            throw new CustomException(ErrorCode.INVALID_ACCESSTOKEN);
        }
    }
}
