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
@Table(name = "users")
public class User extends BaseTimeEntity implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(unique = true)
    private String email;

    @Column(name = "password_hash")
    private String passwordHash;

    private String nickname;

    @Column(name = "is_guest", nullable = false)
    private Boolean isGuest = true;

    @Column(name = "device_id", unique = true)
    private String deviceId;

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
    public User(Long id, String email, String passwordHash, String nickname, Boolean isGuest, String deviceId) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.nickname = nickname;
        this.isGuest = isGuest != null ? isGuest : true;
        this.deviceId = deviceId;
    }

    // --- UserDetails 구현 ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 여기서는 단순하게 "ROLE_USER"를 부여합니다.
        // isGuest에 따라 "ROLE_GUEST" 등으로 분기할 수도 있습니다.
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
    }

    @Override
    public String getPassword() {
        return this.passwordHash;
    }

    @Override
    public String getUsername() {
        // Spring Security에서 사용자를 식별하는 고유 ID
        // 게스트/정회원 모두 고유하고 non-null인 ID를 반환해야 합니다.
        // email은 게스트의 경우 null일 수 있으므로, user_id를 문자열로 반환합니다.
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
