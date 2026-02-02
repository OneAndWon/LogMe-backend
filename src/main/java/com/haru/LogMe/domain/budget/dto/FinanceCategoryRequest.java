package com.haru.LogMe.domain.budget.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class FinanceCategoryRequest {
    @Getter
    @NoArgsConstructor
    public static class FinanceCategoryCreateDto {
        @NotBlank(message = "이름은 필수입니다.")
        private String name;

        @NotBlank(message = "타입은 필수입니다.") // income, expense
        private String type;

        private String icon;
    }

    @Getter
    @NoArgsConstructor
    public static class FinanceCategoryUpdateDto {
        private String name;
        private String type;
        private String icon;
    }
}
