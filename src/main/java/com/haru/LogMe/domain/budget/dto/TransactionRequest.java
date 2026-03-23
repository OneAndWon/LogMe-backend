package com.haru.LogMe.domain.budget.dto;

import com.haru.LogMe.domain.budget.entity.TransactionType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionRequest {
    @Getter
    @NoArgsConstructor
    public static class CreateDto {
        private Long assetId;     // Nullable
        private Long categoryId;  // Nullable

        @NotNull(message = "수입/지출 타입은 필수입니다.")
        private TransactionType type;      // INCOME, EXPENSE, TRANSFER

        @NotNull(message = "금액은 필수입니다.")
        private BigDecimal amount;

        @NotNull(message = "날짜는 필수입니다.")
        private LocalDateTime date;

        private String description;
        private String memo;
    }

    @Getter
    @NoArgsConstructor
    public static class UpdateDto {
        private Long assetId;
        private Long categoryId;
        private TransactionType type;
        private BigDecimal amount;
        private LocalDateTime date;
        private String description;
        private String memo;
    }
}
