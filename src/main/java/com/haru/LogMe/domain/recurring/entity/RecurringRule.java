package com.haru.LogMe.domain.recurring.entity;

import com.haru.LogMe.domain.common.BaseTimeEntity;
import com.haru.LogMe.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "recurring_rule")
@SQLRestriction("deleted_at IS NULL") // 삭제된 데이터 자동 제외
public class RecurringRule extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "recurring_rule_id")
    private Long recurringRuleId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "target_type", nullable = false)
    private String targetType; // "TODO", "FINANCE"

    @Column(name = "frequency_type", nullable = false)
    private String frequencyType; // "DAY", "WEEK", "MONTH"

    @Column(name = "frequency_value", nullable = false)
    private Integer frequencyValue;

    @Column(name = "base_date", nullable = false)
    private LocalDate baseDate; // 반복 시작 기준일

    @Column(name = "end_date")
    private LocalDate endDate; // Nullable (없으면 무한 스케줄링)

    @Column(nullable = false)
    private String status; // "ACTIVE", "STOPPED"

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt; // Soft Delete 용

    @Builder
    public RecurringRule(User user, String targetType, String frequencyType, Integer frequencyValue, LocalDate baseDate, LocalDate endDate) {
        this.user = user;
        this.targetType = targetType;
        this.frequencyType = frequencyType;
        this.frequencyValue = frequencyValue;
        this.baseDate = baseDate;
        this.endDate = endDate;
        this.status = "ACTIVE"; // 생성 시 기본값
    }

    // 반복 중단(삭제) 처리 메서드
    public void stopRecurring() {
        this.status = "STOPPED";
        this.deletedAt = LocalDateTime.now();
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }
}
