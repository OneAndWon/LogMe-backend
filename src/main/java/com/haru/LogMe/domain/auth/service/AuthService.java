package com.haru.LogMe.domain.auth.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.haru.LogMe.domain.auth.dto.AuthRequest;
import com.haru.LogMe.domain.auth.dto.AuthResponse;
import com.haru.LogMe.domain.user.entity.User;
import com.haru.LogMe.domain.user.entity.UserSettings;
import com.haru.LogMe.domain.user.repository.UserRepository;
import com.haru.LogMe.global.jwt.JwtTokenProvider;
import com.haru.LogMe.global.util.GoogleTokenVerifier;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    private final GoogleTokenVerifier googleTokenVerifier;

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

    @Transactional
    public AuthResponse.TokenDto loginWithGoogle(AuthRequest.GoogleLoginDto request) {
        // 1. 구글 토큰 검증 및 정보 추출
        GoogleIdToken.Payload payload = googleTokenVerifier.verify(request.getIdToken());
        String email = payload.getEmail();
        String name = (String) payload.get("name");
        String socialId = payload.getSubject(); // 구글의 고유 식별자 (socialId)

        // 2. Provider와 Social ID로 유저 조회 (없으면 신규 회원가입)
        User user = userRepository.findByProviderAndSocialId("google", socialId)
                .orElseGet(() -> createSocialUser(email, name, "google", socialId));

        // 3. 기존 JWT 발급 로직 그대로 사용
        String accessToken = jwtTokenProvider.generateAccessToken(user);

        // 4. 응답 반환
        return new AuthResponse.TokenDto(accessToken, user);
    }

    // 소셜 전용 유저 생성 메서드
    private User createSocialUser(String email, String nickname, String provider, String socialId) {
        User newUser = User.builder()
                .email(email)
                .nickname(nickname)
                .provider(provider)
                .socialId(socialId)
                .isGuest(false) // 정회원 처리
                // deviceId는 소셜 로그인 유저이므로 null로 설정
                .build();

        // 기존처럼 설정(UserSettings) 초기화
        UserSettings defaultSettings = UserSettings.builder()
                .user(newUser)
                .notificationSettings(new HashMap<>())
                .build();
        newUser.setUserSettings(defaultSettings);

        return userRepository.save(newUser);
    }
}
