package com.haru.LogMe.domain.budget.dto;

import com.haru.LogMe.domain.budget.entity.Budget;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
public class BudgetResponse {
    private Long budgetId;
    private Long categoryId;
    private String categoryName; // UI 편의성을 위해 포함
    private String yearMonth;
    private BigDecimal amount;

    public static BudgetResponse from(Budget budget) {
        return BudgetResponse.builder()
                .budgetId(budget.getBudgetId())
                .categoryId(budget.getCategory().getFinanceCategoryId())
                .categoryName(budget.getCategory().getName())
                .yearMonth(budget.getYearMonth())
                .amount(budget.getAmount())
                .build();
    }
}
