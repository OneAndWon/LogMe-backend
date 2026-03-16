package com.haru.LogMe.domain.auth.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ConvertRequest {
    private String socialAccessToken;
    private String provider; // "google", "apple", "kakao" 등
    private String nickname;
}
