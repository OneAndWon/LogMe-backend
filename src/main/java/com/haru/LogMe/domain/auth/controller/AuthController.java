package com.haru.LogMe.domain.auth.controller;

import com.haru.LogMe.domain.auth.dto.AuthRequest;
import com.haru.LogMe.domain.auth.dto.AuthResponse;
import com.haru.LogMe.domain.auth.service.AuthService;
import com.haru.LogMe.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/logme/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/quest")
    public ResponseEntity<ApiResponse<AuthResponse.TokenDto>> guestLogin(
            @Valid @RequestBody AuthRequest.GuestLoginDto request
            ) {
        AuthResponse.TokenDto tokenData = authService.loginAsGuest(request);

        return ResponseEntity.ok(
                ApiResponse.ok(tokenData)
        );
    }
}
