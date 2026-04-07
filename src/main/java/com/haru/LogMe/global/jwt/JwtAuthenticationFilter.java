package com.haru.LogMe.global.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.util.HashMap;
import java.util.Map;

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
    private final ObjectMapper objectMapper;

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

            // 응답 헤더 설정 (JSON 형식, 한글 깨짐 방지, 401 상태 코드)
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            // 명세서에 맞는 JSON 형태 만들기
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("data", null);

            Map<String, String> errorDetail = new HashMap<>();
            errorDetail.put("code", e.getErrorCode().name()); // 예: "INVALID_ACCESSTOKEN"
            errorDetail.put("message", "유효하지 않은 접근입니다."); // 클라이언트에게 보여줄 메시지

            errorResponse.put("error", errorDetail);

            // 생성한 JSON을 클라이언트에게 바로 응답으로 전송하고 필터 강제 종료
            response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
            return;
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
