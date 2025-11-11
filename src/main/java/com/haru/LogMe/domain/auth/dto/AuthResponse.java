package com.haru.LogMe.domain.auth.dto;

import com.haru.LogMe.domain.user.entity.User;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

public class AuthResponse {

    @Getter
    public static class TokenDto {
        @Schema(description = "guest 용 Access Token")
        private final String accessToken;

        @Schema(description = "발급된 유저 정보")
        private final UserDto user;

        public TokenDto(String accessToken, User user) {
            this.accessToken = accessToken;
            this.user = new UserDto(user);
        }
    }

    @Getter
    public static class UserDto {
        @Schema(description = "유저 ID")
        private final Long userId;

        @Schema(description = "게스트 여부")
        private final boolean isGuest;

        @Schema(description = "닉네임", nullable = true)
        private final String nickname;

        public UserDto(User user) {
            this.userId = user.getId();
            this.isGuest = user.getIsGuest();
            this.nickname = user.getNickname();
        }
    }
}
