package com.haru.LogMe.domain.budget.entity;

import com.haru.LogMe.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(
        name = "budget",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_budget_user_category_month",
                        columnNames = {"user_id", "category_id", "year_month"}
                )
        }
)
public class Budget {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "budget_id")
    private Long budgetId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private FinanceCategory category;

    // "2025-11" 형식으로 저장
    @Column(name = "year_month", nullable = false)
    private String yearMonth;

    @Column(nullable = false)
    private BigDecimal amount;

    @Builder
    public Budget(User user, FinanceCategory category, String yearMonth, BigDecimal amount) {
        this.user = user;
        this.category = category;
        this.yearMonth = yearMonth;
        this.amount = amount;
    }

    // 예산 금액 수정 비즈니스 로직
    public void updateAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
