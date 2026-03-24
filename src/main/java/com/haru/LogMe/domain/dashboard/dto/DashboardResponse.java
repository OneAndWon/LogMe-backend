package com.haru.LogMe.domain.dashboard.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.haru.LogMe.domain.diary.entity.Emotion;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DashboardResponse {
    private LocalDate date;
    private String motto;

    @JsonProperty("todo_summary")
    private TodoSummaryDto todoSummary;

    @JsonProperty("diary_summary")
    private DiarySummaryDto diarySummary;

    @JsonProperty("budget_summary")
    private BudgetSummaryDto budgetSummary;

    private List<TimelineItemDto> timeline;

    // --- 내부 정적 클래스 ---

    @Getter
    @Builder
    public static class TodoSummaryDto {
        @JsonProperty("total_count")
        private long totalCount;

        @JsonProperty("completed_count")
        private long completedCount;

        @JsonProperty("upcoming_todos")
        private List<UpcomingTodoDto> upcomingTodos;
    }

    @Getter
    @Builder
    public static class UpcomingTodoDto {
        private String time;
        private String title;
    }

    @Getter
    @Builder
    public static class DiarySummaryDto {
        @JsonProperty("has_diary")
        private boolean hasDiary;

        @JsonProperty("emotion_icon")
        private Emotion emotionIcon;

        @JsonProperty("title")
        private String title;
    }

    @Getter
    @Builder
    public static class BudgetSummaryDto {
        @JsonProperty("total_expense")
        private long totalExpense;

        @JsonProperty("total_income")
        private long totalIncome;
    }

    @Getter
    @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class TimelineItemDto {
        private String type; // 'TODO', 'TRANSACTION'
        private String time; // 'HH:mm'
        private String title;

        @JsonProperty("is_completed")
        private Boolean isCompleted;

        private Long amount;

        @JsonProperty("category_name")
        private String categoryName;
    }
}
