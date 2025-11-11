package com.haru.LogMe.domain.user.entity;

import com.haru.LogMe.domain.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "users",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"provider", "social_id"})
        }
)
public class User extends BaseTimeEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column
    private String email;

    private String nickname;

    @Column(name = "is_guest", nullable = false)
    private Boolean isGuest = true;

    @Column(name = "device_id", unique = true)
    private String deviceId;

    @Column(name = "social_id")
    private String socialId;

    @Column(name = "provider")
    private String provider; // 예: "kakao", "google", "apple"

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private UserSettings userSettings;

    // 연관관계 편의 메서드
    public void setUserSettings(UserSettings userSettings) {
        this.userSettings = userSettings;
        if (userSettings != null) {
            userSettings.setUser(this);
        }
    }

    @Builder
    public User(Long id, String email, String nickname,
                Boolean isGuest, String deviceId, String socialId, String provider) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.isGuest = isGuest != null ? isGuest : true;
        this.deviceId = deviceId;
        this.socialId = socialId;
        this.provider = provider;
    }

    // --- UserDetails 구현 ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 비회원과 정회원 권한 분기
        if (Boolean.TRUE.equals(this.isGuest)) {
            return Collections.singletonList(new SimpleGrantedAuthority("ROLE_GUEST"));
        }
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return null;
    }

    @Override
    public String getUsername() {
        // Spring Security의 식별자
        // 1. 소셜 로그인 유저
        if (this.socialId != null && this.provider != null)
            return this.provider + "_" + this.socialId;

        // 2. 비회원(게스트) 유저
        if (this.deviceId != null) return "guest_" + this.deviceId;

        // 3. (Fallback) 이메일
        if (this.email != null) return this.email;

        // 4. (최후 Fallback)
        return String.valueOf(this.id);
    }

    // 계정 상태 관련 메서드 (일단 모두 true로 반환)

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
