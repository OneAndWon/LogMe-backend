package com.haru.LogMe.domain.todo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty("is_completed")
    private Boolean isCompleted;

    @JsonProperty("due_date")
    private LocalDateTime dueDate;

    @JsonProperty("alarm_time")
    private LocalDateTime alarmTime;

    @JsonProperty("recurring_rule")
    private String recurringRule;
}
