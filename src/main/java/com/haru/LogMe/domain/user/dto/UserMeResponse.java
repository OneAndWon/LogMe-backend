package com.haru.LogMe.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.haru.LogMe.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserMeResponse {

    @JsonProperty("user_id") // json 나갈 때 userId -> user_id
    private Long userId;

    private String email;

    private String nickname;

    @JsonProperty("is_guest") // json 나갈 때 isGuest -> is_guest
    private Boolean isGuest;

    public static UserMeResponse from(User user) {
        return UserMeResponse.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .nickname(user.getNickname())
                .isGuest(user.getIsGuest())
                .build();
    }
}
