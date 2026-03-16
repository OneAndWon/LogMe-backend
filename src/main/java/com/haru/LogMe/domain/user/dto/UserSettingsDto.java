package com.haru.LogMe.domain.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.haru.LogMe.domain.user.entity.UserSettings;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserSettingsDto {
    @JsonProperty("notification_settings")
    private Map<String, Object> notificationSettings;

    public static UserSettingsDto from(UserSettings userSettings) {
        return new UserSettingsDto(userSettings.getNotificationSettings());
    }
}
