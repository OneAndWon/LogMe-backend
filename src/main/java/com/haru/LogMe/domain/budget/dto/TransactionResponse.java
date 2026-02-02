package com.haru.LogMe.domain.budget.dto;

import com.haru.LogMe.domain.budget.entity.Transaction;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Builder
public class TransactionResponse {
    private Long transactionId;
    private Long assetId;
    private String assetName;    // 편의상 이름도 함께 반환
    private Long categoryId;
    private String categoryName; // 편의상 이름도 함께 반환
    private String type;
    private BigDecimal amount;
    private LocalDateTime date;
    private String description;
    private String memo;

    public static TransactionResponse from(Transaction transaction) {
        return TransactionResponse.builder()
                .transactionId(transaction.getTransactionId())
                .assetId(transaction.getAsset() != null ? transaction.getAsset().getAssetId() : null)
                .assetName(transaction.getAsset() != null ? transaction.getAsset().getName() : null)
                .categoryId(transaction.getCategory() != null ? transaction.getCategory().getFinanceCategoryId() : null)
                .categoryName(transaction.getCategory() != null ? transaction.getCategory().getName() : null)
                .type(transaction.getType())
                .amount(transaction.getAmount())
                .date(transaction.getDate())
                .description(transaction.getDescription())
                .memo(transaction.getMemo())
                .build();
    }
}
