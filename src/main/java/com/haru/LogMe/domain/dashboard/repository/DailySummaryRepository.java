package com.haru.LogMe.domain.dashboard.repository;

import com.haru.LogMe.domain.dashboard.entity.DailySummary;
import com.haru.LogMe.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface DailySummaryRepository extends JpaRepository<DailySummary, Long> {
    Optional<DailySummary> findByUserAndDate(User user, LocalDate date);


}
