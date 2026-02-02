package com.haru.LogMe.domain.budget.entity;

import com.haru.LogMe.domain.common.BaseTimeEntity;
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
@Table(name = "asset")
public class Asset extends BaseTimeEntity { // 상속 추가
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "asset_id")
    private Long assetId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String name;

    @Column(name = "type") // bank, card, cash
    private String type;

    @Column(name = "initial_balance")
    private BigDecimal initialBalance;

    @Builder
    public Asset(User user, String name, String type, BigDecimal initialBalance) {
        this.user = user;
        this.name = name;
        this.type = type;
        this.initialBalance = initialBalance != null ? initialBalance : BigDecimal.ZERO;
    }

    public void update(String name, String type, BigDecimal initialBalance) {
        if (name != null) this.name = name;
        if (type != null) this.type = type;
        if (initialBalance != null) this.initialBalance = initialBalance;
    }
}