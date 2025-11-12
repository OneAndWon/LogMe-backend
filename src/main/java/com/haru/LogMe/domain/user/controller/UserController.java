package com.haru.LogMe.domain.user.controller;

import com.haru.LogMe.domain.user.dto.UserMeResponse;
import com.haru.LogMe.domain.user.entity.User;
import com.haru.LogMe.domain.user.service.UserService;
import com.haru.LogMe.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
