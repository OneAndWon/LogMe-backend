package com.haru.LogMe.domain.auth.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class AuthRequest {

    @Getter
    public static class GuestLoginDto {
        @JsonProperty("device_id")
        @NotBlank(message = "device_id는 필수입니다.")
        private final String deviceId;

        // @JsonCreator를 통해 스프링(Jackson)이 객체를 만들 때 이 생성자를 쓰도록 지시
        @JsonCreator
        public GuestLoginDto(@JsonProperty("device_id") String deviceId) {
            this.deviceId = deviceId;
        }
    }

    @Getter
    public static class GoogleLoginDto {
        @JsonProperty("id_token")
        @NotBlank(message = "구글 id_token은 필수입니다.")
        private final String idToken;

        @JsonCreator
        public GoogleLoginDto(@JsonProperty("id_token") String idToken) {
            this.idToken = idToken;
        }
    }
}
