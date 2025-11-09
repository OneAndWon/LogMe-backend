package com.haru.LogMe.global.jwt;

import com.haru.LogMe.global.exception.CustomException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 매 요청마다 Request Header의 Authorization 헤더에서 토큰을 꺼내 검증하고,
 * 유효하다면 SecurityContextHolder에 Authentication 객체를 저장합니다.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Request Header에서 토큰 추출
        String token = resolveToken(request);

        try {
            // 2. validateToken으로 토큰 유효성 검사
            // (StringUtils.hasText(token)은 토큰이 null이거나 비어있지 않은지 확인)
            if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token)) {

                // 3. 토큰이 유효할 경우, 토큰에서 Authentication 객체를 가져와 SecurityContext에 저장
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (CustomException e) { // ⬅️ JwtTokenProvider의 validateToken에서 던지는 예외
            // validateToken에서 예외 발생 시, SecurityContext를 비웁니다.
            SecurityContextHolder.clearContext();
            // (필요시) 여기에 response에 401 에러를 직접 응답하는 로직을 추가할 수 있습니다.
            // (예: jwtAuthenticationEntryPoint.commence(request, response, new InsufficientAuthenticationException("...")))
        }

        // 4. 다음 필터로 요청 전달
        filterChain.doFilter(request, response);
    }

    /**
     * Request Header에서 "Authorization" 헤더를 꺼내 "Bearer " 접두사를 제거하고 토큰만 반환합니다.
     */
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
