package com.haru.LogMe.domain.todo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.haru.LogMe.domain.todo.entity.Todo;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TodoResponse {
    @JsonProperty("todo_id")
    private Long todoId;

    @JsonProperty("user_id")
    private Long userId; // 응답 시 user_id도 주는 것이 좋음

    @JsonProperty("category_id")
    private Long categoryId;

    @JsonProperty("parent_todo_id")
    private Long parentTodoId;

    private String title;
    private String memo;

    @JsonProperty("is_completed")
    private Boolean isCompleted;

    @JsonProperty("due_date")
    private LocalDateTime dueDate;

    @JsonProperty("alarm_time")
    private LocalDateTime alarmTime;

    @JsonProperty("recurring_rule")
    private String recurringRule;

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    public TodoResponse(Todo todo) {
        this.todoId = todo.getId();
        this.userId = todo.getUser().getUserId();
        this.categoryId = todo.getCategoryId();
        this.parentTodoId = todo.getParentTodoId();
        this.title = todo.getTitle();
        this.memo = todo.getMemo();
        this.isCompleted = todo.getIsCompleted();
        this.dueDate = todo.getDueDate();
        this.alarmTime = todo.getAlarmTime();
        this.recurringRule = todo.getRecurringRule();
        this.createdAt = todo.getCreatedAt();
        this.updatedAt = todo.getUpdatedAt();
    }
}
