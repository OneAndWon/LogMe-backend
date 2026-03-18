package com.haru.LogMe.domain.todo.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.haru.LogMe.domain.todo.entity.Todo;
import com.haru.LogMe.domain.todo.entity.TodoPriority;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TodoResponse {
    @JsonProperty("todo_id")
    private Long todoId;

    @JsonProperty("user_id")
    private Long userId; // 응답 시 user_id도 주는 것이 좋음

    @JsonProperty("category_id")
    private Long categoryId;

    @JsonProperty("parent_todo_id")
    private Long parentTodoId;

    @JsonProperty("recurring_id")
    private Long recurringId;

    private String title;
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

    @JsonProperty("created_at")
    private LocalDateTime createdAt;

    @JsonProperty("updated_at")
    private LocalDateTime updatedAt;

    @JsonProperty("sub_todos")
    private List<TodoResponse> subTodos; // 하위 할 일 목록

    public TodoResponse(Todo todo) {
        this.todoId = todo.getId();
        this.userId = todo.getUser().getUserId();
        this.categoryId = todo.getCategoryId();
        this.parentTodoId = todo.getParentTodoId();
        this.recurringId = todo.getRecurringId();
        this.title = todo.getTitle();
        this.memo = todo.getMemo();
        this.priority = todo.getPriority();
        this.isCompleted = todo.getIsCompleted();
        this.startDate = todo.getStartDate();
        this.dueDate = todo.getDueDate();
        this.alarmTime = todo.getAlarmTime();
        this.createdAt = todo.getCreatedAt();
        this.updatedAt = todo.getUpdatedAt();
    }

    //하위 할 일 목록을 세팅하는 편의 메서드
    public void setSubTodos(List<TodoResponse> subTodos) {
        this.subTodos = subTodos;
    }
}
