package com.haru.LogMe.domain.budget.entity;

import com.haru.LogMe.domain.common.BaseTimeEntity;
import com.haru.LogMe.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "finance_category")
public class FinanceCategory extends BaseTimeEntity { // 상속 추가

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "finance_category_id")
    private Long financeCategoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String name;

    @Column(name = "type") // income, expense
    private String type;

    private String icon;

    @Builder
    public FinanceCategory(User user, String name, String type, String icon) {
        this.user = user;
        this.name = name;
        this.type = type;
        this.icon = icon;
    }

    public void update(String name, String type, String icon) {
        if (name != null) this.name = name;
        if (type != null) this.type = type;
        if (icon != null) this.icon = icon;
    }
}