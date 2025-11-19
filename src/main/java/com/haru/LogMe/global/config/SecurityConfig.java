package com.haru.LogMe.global.config;

import com.haru.LogMe.global.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity // Spring Security 활성화
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    // 1. PasswordEncoder Bean 등록
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 2. AuthenticationManager Bean 등록 (로그인 API에서 사용)
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    // 3. SecurityFilterChain Bean 등록 (핵심 설정)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 3-1. CSRF, HTTP Basic 인증 비활성화
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                // 3-2. 세션 관리를 STATELESS로 설정 (JWT 사용)
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 3-3. (선택 사항) 커스텀 예외 처리 핸들러 등록
                // .exceptionHandling(ex -> ex
                //         .authenticationEntryPoint(jwtAuthenticationEntryPoint) // 401 (인증 실패)
                //         .accessDeniedHandler(jwtAccessDeniedHandler)        // 403 (인가 실패)
                // )

                // 3-4. HTTP 요청 권한 설정
                .authorizeHttpRequests(auth -> auth
                        // "/logme/auth/**" (회원가입/로그인)는 인증 없이 허용
                        .requestMatchers("/logme/auth/**").permitAll()

                        // Swagger UI 관련 엔드포인트 허용
                        .requestMatchers(
                                "/swagger-ui.html",  // Swagger UI 페이지
                                "/swagger-ui/**",    // Swagger UI 리소스 (CSS, JS)
                                "/api-docs/**",      // application.yml에서 설정한 경로
                                "/v3/api-docs/**"    // Swagger 설정 파일 경로
                        ).permitAll()

                        // (추가) 비회원(GUEST) 또는 정회원(USER)만 접근 가능
                        .requestMatchers(
                                "/logme/todos/**",   // Todo
                                "/logme/diaries/**",  // Diary
                                "/logme/transactions/**", // Transaction
                                "/logme/users/me"    // 내 정보 조회
                        ).hasAnyAuthority("ROLE_GUEST", "ROLE_USER")

                        // 그 외 모든 요청은 인증 필요
                        .anyRequest().authenticated())

                // 3-5. 우리가 만든 JWT 필터를 Spring Security 필터 체인에 등록
                // (UsernamePasswordAuthenticationFilter *전에* 실행되어야 함)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
