package com.haru.LogMe.domain.todo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class TodoRequest {
    @NotBlank(message = "제목은 필수입니다.")
    private String title;

    @JsonProperty("category_id")
    private Long categoryId;

    @JsonProperty("parent_todo_id")
    private Long parentTodoId;

    private String memo;

    @JsonProperty("is_completed")
    private Boolean isCompleted;

    @JsonProperty("due_date")
    private LocalDateTime dueDate;

    @JsonProperty("alarm_time")
    private LocalDateTime alarmTime;

    @JsonProperty("recurring_rule")
    private String recurringRule;
}
