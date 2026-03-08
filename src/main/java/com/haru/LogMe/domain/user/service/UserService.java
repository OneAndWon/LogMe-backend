package com.haru.LogMe.domain.user.service;

import com.haru.LogMe.domain.user.dto.UserMeResponse;
import com.haru.LogMe.domain.user.dto.UserSettingsDto;
import com.haru.LogMe.domain.user.dto.UserUpdateRequest;
import com.haru.LogMe.domain.user.entity.User;
import com.haru.LogMe.domain.user.repository.UserRepository;
import com.haru.LogMe.global.exception.CustomException;
import com.haru.LogMe.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private  final UserRepository userRepository;

    // 1. 내 정보 조회
    public UserMeResponse getMyInfo(Long userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return UserMeResponse.from(user);
    }

    // 2. 내 정보 수정
    @Transactional
    public UserMeResponse updateMyInfo(Long userId, UserUpdateRequest request) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        user.updateNickname(request.getNickname());

        return UserMeResponse.from(user);
    }

    // 3. 회원 탈퇴
    @Transactional
    public void deleteUser(Long userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        // Cascade.ALL 과 orphanRemoval=true 설정으로 UserSettings도 자동 삭제됨
        userRepository.delete(user);
    }

    // 4. 사용자 설정 조회
    public UserSettingsDto getUserSettings(Long userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        return UserSettingsDto.from(user.getUserSettings());
    }

    // 5. 사용자 설정 수정
    @Transactional
    public UserSettingsDto updateUserSettings(Long userId, UserSettingsDto request) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        user.getUserSettings().updateNotificationSettings(request.getNotificationSettings());

        // 수정된 설정 데이터를 반환
        return UserSettingsDto.from(user.getUserSettings());
    }
}
