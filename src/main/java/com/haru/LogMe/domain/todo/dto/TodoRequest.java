package com.haru.LogMe.domain.todo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.haru.LogMe.domain.todo.entity.TodoPriority;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class TodoRequest {
    
    private String title;

    @JsonProperty("category_id")
    private Long categoryId;

    @JsonProperty("parent_todo_id")
    private Long parentTodoId;

    private String memo;

    private TodoPriority priority; // 'HIGH', 'MEDIUM', 'LOW'

    @JsonProperty("is_completed")
    private Boolean isCompleted;

    @JsonProperty("start_date")
    private LocalDateTime startDate;

    @JsonProperty("due_date")
    private LocalDateTime dueDate;

    @JsonProperty("alarm_time")
    private LocalDateTime alarmTime;

    @JsonProperty("recurring_rule")
    private RecurringRuleDto recurringRule;

    // 반복 규칙을 받기 위한 내부 클래스
    @Getter
    @NoArgsConstructor
    public static class RecurringRuleDto {
        @JsonProperty("frequency_type")
        private String frequencyType; // 'DAY', 'WEEK', 'MONTH'

        @JsonProperty("frequency_value")
        private Integer frequencyValue; // 1, 2, 3 등
    }
}
