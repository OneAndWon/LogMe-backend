package com.haru.LogMe.domain.user.controller;

import com.haru.LogMe.domain.user.dto.UserMeResponse;
import com.haru.LogMe.domain.user.dto.UserSettingsDto;
import com.haru.LogMe.domain.user.dto.UserUpdateRequest;
import com.haru.LogMe.domain.user.entity.User;
import com.haru.LogMe.domain.user.service.UserService;
import com.haru.LogMe.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/logme/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ApiResponse<UserMeResponse> getMyInfo(@AuthenticationPrincipal User user) {
        UserMeResponse response = userService.getMyInfo(user.getUserId());

        return ApiResponse.ok(response);
    }

    @PutMapping("/me")
    public ApiResponse<UserMeResponse> updateMyInfo(
            @AuthenticationPrincipal User user,
            @RequestBody UserUpdateRequest request) {
        UserMeResponse response = userService.updateMyInfo(user.getUserId(), request);
        return ApiResponse.ok(response);
    }

    @DeleteMapping("/me")
    public ApiResponse<Map<String, String>> deleteUser(@AuthenticationPrincipal User user) {
        userService.deleteUser(user.getUserId());
        // 명세서에 맞게 성공 메시지를 Map으로 감싸서 반환
        return ApiResponse.ok(Map.of("message", "회원 탈퇴가 성공적으로 처리되었습니다."));
    }

    @GetMapping("/me/settings")
    public ApiResponse<UserSettingsDto> getUserSettings(@AuthenticationPrincipal User user) {
        UserSettingsDto response = userService.getUserSettings(user.getUserId());
        return ApiResponse.ok(response);
    }

    @PutMapping("/me/settings")
    public ApiResponse<UserSettingsDto> updateUserSettings(
            @AuthenticationPrincipal User user,
            @RequestBody UserSettingsDto request) {
        UserSettingsDto response = userService.updateUserSettings(user.getUserId(), request);
        return ApiResponse.ok(response);
    }
}
