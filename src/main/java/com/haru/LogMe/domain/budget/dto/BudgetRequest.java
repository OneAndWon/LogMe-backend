package com.haru.LogMe.domain.budget.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

public class BudgetRequest {
    @Getter
    @NoArgsConstructor
    public static class BudgetSaveDto { // 생성 및 수정 공통 (Upsert)
        @NotNull(message = "카테고리 ID는 필수입니다.")
        private Long categoryId;

        @NotBlank(message = "년월은 필수입니다.")
        @Pattern(regexp = "^\\d{4}-\\d{2}$", message = "년월 형식은 YYYY-MM 이어야 합니다.")
        private String yearMonth; // 예: "2025-11"

        @NotNull(message = "예산 금액은 필수입니다.")
        @Min(value = 0, message = "예산 금액은 0원 이상이어야 합니다.")
        private BigDecimal amount;
    }
}
