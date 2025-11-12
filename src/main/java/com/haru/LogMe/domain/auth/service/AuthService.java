package com.haru.LogMe.domain.auth.service;

import com.haru.LogMe.domain.auth.dto.AuthRequest;
import com.haru.LogMe.domain.auth.dto.AuthResponse;
import com.haru.LogMe.domain.user.entity.User;
import com.haru.LogMe.domain.user.entity.UserSettings;
import com.haru.LogMe.domain.user.repository.UserRepository;
import com.haru.LogMe.global.jwt.JwtTokenProvider;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public AuthResponse.TokenDto loginAsGuest(AuthRequest.GuestLoginDto request){
        String deviceId = request.getDeviceId();

        // 1. 기기 ID로 사용자 조회
        User user = userRepository.findByDeviceId(deviceId)
                .orElseGet(() -> createGuestUser(deviceId));

        // 2. JWT 토큰 생성
        String accessToken = jwtTokenProvider.generateAccessToken(user);

        // 3. 응답 생성
        return new AuthResponse.TokenDto(accessToken, user);
    }

    private User createGuestUser(String deviceId) {
        User newUser = User.builder()
                .deviceId(deviceId)
                .isGuest(true)
                .nickname(null)
                .build();

        //user settings 생성
        UserSettings defaultSettings = UserSettings.builder()
                .user(newUser)
                .notificationSettings(new HashMap<>())
                .build();

        //user와 user settings 상호 연결
        newUser.setUserSettings(defaultSettings);

        //user 저장. -> cascade = ALL 옵션에 의해 UserSettings도 함께 저장됨
        return userRepository.save(newUser);
    }
}
