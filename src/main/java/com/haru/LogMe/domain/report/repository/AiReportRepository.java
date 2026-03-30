package com.haru.LogMe.domain.report.repository;

import com.haru.LogMe.domain.report.entity.AiReport;
import com.haru.LogMe.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface AiReportRepository extends JpaRepository<AiReport, Long> {
    Optional<AiReport> findByUserAndTypeAndStartDateAndEndDate(User user, String type, LocalDate startDate, LocalDate endDate);
}
