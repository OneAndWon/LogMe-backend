package com.haru.LogMe.domain.report.entity;

import com.haru.LogMe.domain.common.BaseTimeEntity;
import com.haru.LogMe.domain.user.entity.User;
import io.hypersistence.utils.hibernate.type.json.JsonType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.time.LocalDate;
import java.util.Map;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ai_report")
public class AiReport extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ai_report_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, length = 20)
    private String type; // 'WEEKLY', 'MONTHLY'

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(nullable = false, length = 20)
    private String status; // 'PENDING', 'COMPLETED', 'FAILED'

    @Type(JsonType.class)
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> content; // AI 분석 결과 본문

    @Column(name = "total_tokens")
    private Integer totalTokens;

    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;

    @Column(name = "model_version", length = 50)
    private String modelVersion;

    @Builder
    public AiReport(User user, String type, LocalDate startDate, LocalDate endDate, String status) {
        this.user = user;
        this.type = type;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    public void updateStatusToCompleted(Map<String, Object> content, Integer totalTokens, String modelVersion) {
        this.status = "COMPLETED";
        this.content = content;
        this.totalTokens = totalTokens;
        this.modelVersion = modelVersion;
    }

    public void updateStatusToFailed(String errorMessage) {
        this.status = "FAILED";
        this.errorMessage = errorMessage;
    }


}
