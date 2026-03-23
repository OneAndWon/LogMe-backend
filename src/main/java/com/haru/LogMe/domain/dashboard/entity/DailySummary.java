package com.haru.LogMe.domain.dashboard.entity;

import com.haru.LogMe.domain.common.BaseTimeEntity;
import com.haru.LogMe.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "daily_summary", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "date"})
})
public class DailySummary extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "daily_summary_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDate date;

    @Column(length = 100)
    private String motto;

    @Builder
    public DailySummary(User user, LocalDate date, String motto) {
        this.user = user;
        this.date = date;
        this.motto = motto;
    }

    public void updateMotto(String motto) {
        this.motto = motto;
    }
}
