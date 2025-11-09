package com.haru.LogMe.domain.user.repository;

import com.haru.LogMe.domain.user.entity.UserSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserSettingRepository extends JpaRepository<UserSettings, Long> {
}
