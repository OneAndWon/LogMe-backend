package com.haru.LogMe.domain.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AuthRequest {

    @Getter
    @NoArgsConstructor
    public static class GuestLoginDto {
        @NotBlank(message = "device_id는 필수입니다.")
        private String deviceId;
    }
}
