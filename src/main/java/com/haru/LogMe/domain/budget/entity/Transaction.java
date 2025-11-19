package com.haru.LogMe.domain.budget.entity;

import com.haru.LogMe.domain.common.BaseTimeEntity;
import com.haru.LogMe.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "transaction")
public class Transaction extends BaseTimeEntity { // 상속 추가
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private Long transactionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "asset_id")
    private Asset asset;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private FinanceCategory category;

    @Column(nullable = false)
    private String type; // income, expense

    @Column(nullable = false)
    private BigDecimal amount;

    @Column(nullable = false)
    private LocalDateTime date; // 실제 소비/수입 날짜

    private String description;

    private String memo;

    @Builder
    public Transaction(User user, Asset asset, FinanceCategory category, String type, BigDecimal amount, LocalDateTime date, String description, String memo) {
        this.user = user;
        this.asset = asset;
        this.category = category;
        this.type = type;
        this.amount = amount;
        this.date = date;
        this.description = description;
        this.memo = memo;
    }

    // 수정 메서드 (Dirty Checking 용)
    public void update(Asset asset, FinanceCategory category, String type, BigDecimal amount, LocalDateTime date, String description, String memo) {
        this.asset = asset;
        this.category = category;
        this.type = type;
        this.amount = amount;
        this.date = date;
        this.description = description;
        this.memo = memo;
    }
}