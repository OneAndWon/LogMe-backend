package com.haru.LogMe.domain.auth.controller;

import com.haru.LogMe.domain.auth.dto.AuthRequest;
import com.haru.LogMe.domain.auth.dto.AuthResponse;
import com.haru.LogMe.domain.auth.dto.ConvertRequest;
import com.haru.LogMe.domain.auth.service.AuthService;
import com.haru.LogMe.domain.user.entity.User;
import com.haru.LogMe.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/logme/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/guest")
    public ResponseEntity<ApiResponse<AuthResponse.TokenDto>> guestLogin(
            @Valid @RequestBody AuthRequest.GuestLoginDto request
            ) {
        AuthResponse.TokenDto tokenData = authService.loginAsGuest(request);

        return ResponseEntity.ok(
                ApiResponse.ok(tokenData)
        );
    }

    @PostMapping("/google")
    public ResponseEntity<ApiResponse<AuthResponse.TokenDto>> googleLogin(
            @Valid @RequestBody AuthRequest.GoogleLoginDto request
    ) {
        AuthResponse.TokenDto tokenData = authService.loginWithGoogle(request);

        return ResponseEntity.ok(
                ApiResponse.ok(tokenData)
        );
    }

    @PostMapping("/convert")
    public ResponseEntity<ApiResponse<AuthResponse.TokenDto>> convertGuestToMember(
            @AuthenticationPrincipal User user, // 현재 비회원 토큰에서 추출된 유저 객체
            @Valid @RequestBody ConvertRequest request
    ) {
        AuthResponse.TokenDto tokenData = authService.convertGuestToMember(user.getUserId(), request);

        return ResponseEntity.ok(
                ApiResponse.ok(tokenData)
        );
    }
}
