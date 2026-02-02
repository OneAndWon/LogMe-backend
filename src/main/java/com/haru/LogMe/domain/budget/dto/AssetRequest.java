package com.haru.LogMe.domain.budget.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

public class AssetRequest {
    @Getter
    @NoArgsConstructor
    public static class AssetCreateDto {
        @NotBlank(message = "자산 이름은 필수입니다.")
        private String name;

        @NotBlank(message = "자산 타입은 필수입니다.") // bank, card, cash
        private String type;
        private BigDecimal initialBalance;
    }

    @Getter
    @NoArgsConstructor
    public static class AssetUpdateDto {
        private String name;
        private String type;
        private BigDecimal initialBalance;
    }
}

