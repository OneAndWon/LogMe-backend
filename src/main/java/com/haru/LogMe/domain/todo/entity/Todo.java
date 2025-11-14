package com.haru.LogMe.domain.todo.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "todo")
public class Todo {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "todo_id")
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "category_id")
    private Long categoryId; // Nullable

    @Column(name = "parent_todo_id")
    private Long parentTodoId; // Nullable (상위 할 일 ID)

    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String memo;

    @Column(name = "is_completed")
    private Boolean isCompleted;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "alarm_time")
    private LocalDateTime alarmTime; // 알림 시간

    @Column(name = "recurring_rule")
    private String recurringRule; // 반복 규칙 (ex: "FREQ=DAILY")

    @Builder
    public Todo(Long userId, Long categoryId, Long parentTodoId, String title, String memo,
                Boolean isCompleted, LocalDateTime dueDate, LocalDateTime alarmTime, String recurringRule) {
        this.userId = userId;
        this.categoryId = categoryId;
        this.parentTodoId = parentTodoId;
        this.title = title;
        this.memo = memo;
        this.isCompleted = (isCompleted != null) ? isCompleted : false; // Default false
        this.dueDate = dueDate;
        this.alarmTime = alarmTime;
        this.recurringRule = recurringRule;
    }

    // PATCH용 수정 메서드 (모든 필드에 대해 null 체크 후 업데이트)
    public void update(Long categoryId, Long parentTodoId, String title, String memo,
                       Boolean isCompleted, LocalDateTime dueDate, LocalDateTime alarmTime, String recurringRule) {
        if (categoryId != null) this.categoryId = categoryId;
        // parentTodoId는 null이 들어오면 "상위 할 일 해제"로 볼지, "변경 없음"으로 볼지 정책에 따라 다르지만,
        // 보통 PATCH에서는 null이면 "변경 없음"으로 처리합니다.
        if (parentTodoId != null) this.parentTodoId = parentTodoId;
        if (title != null) this.title = title;
        if (memo != null) this.memo = memo;
        if (isCompleted != null) this.isCompleted = isCompleted;
        if (dueDate != null) this.dueDate = dueDate;
        if (alarmTime != null) this.alarmTime = alarmTime;
        if (recurringRule != null) this.recurringRule = recurringRule;
    }

}
