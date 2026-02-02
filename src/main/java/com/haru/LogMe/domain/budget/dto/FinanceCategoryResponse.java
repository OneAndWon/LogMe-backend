package com.haru.LogMe.domain.budget.dto;

import com.haru.LogMe.domain.budget.entity.FinanceCategory;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class FinanceCategoryResponse {
    private Long categoryId;
    private String name;
    private String type;
    private String icon;

    public static FinanceCategoryResponse from(FinanceCategory category) {
        return FinanceCategoryResponse.builder()
                .categoryId(category.getFinanceCategoryId())
                .name(category.getName())
                .type(category.getType())
                .icon(category.getIcon())
                .build();
    }
}
