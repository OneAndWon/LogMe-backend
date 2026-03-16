package com.haru.LogMe.domain.user.entity;

import io.hypersistence.utils.hibernate.type.json.JsonBinaryType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Type;

import java.util.HashMap;
import java.util.Map;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "user_settings")
public class UserSettings {
    @Id
    @Column(name = "user_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // user_id를 PK이자 FK로 사용
    @JoinColumn(name = "user_id")
    @Setter // 연관관계 편의 메서드를 위해 User에서만 호출하도록 설정
    private User user;

    @Type(JsonBinaryType.class)
    @Column(name = "notification_settings", columnDefinition = "jsonb")
    private Map<String, Object> notificationSettings = new HashMap<>();

    //
    @Builder
    public UserSettings(User user, Map<String, Object> notificationSettings) {
        this.user = user;
        this.notificationSettings = (notificationSettings != null) ? notificationSettings : new HashMap<>();
    }

    public void updateNotificationSettings(Map<String, Object> notificationSettings) {
        this.notificationSettings = notificationSettings;
    }
}
