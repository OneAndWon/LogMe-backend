package com.haru.LogMe.domain.user.repository;

import com.haru.LogMe.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    /**
     * 이메일로 사용자를 찾음. (정회원 로그인 시 사용)
     */
    Optional<User> findByEmail(String email);

    /**
     * 기기 ID로 사용자를 찾음. (비회원 식별 시 사용)
     */
    Optional<User> findByDeviceId(String deviceId);

    /**
     * 사용자 ID로 사용자를 찾음.
     */
    Optional<User> findByUserId(Long userId);
}
