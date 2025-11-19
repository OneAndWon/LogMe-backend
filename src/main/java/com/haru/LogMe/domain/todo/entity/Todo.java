package com.haru.LogMe.domain.todo.entity;

import com.haru.LogMe.domain.common.BaseTimeEntity;
import com.haru.LogMe.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "todo")
public class Todo extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "todo_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

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
    public Todo(User user, Long categoryId, Long parentTodoId, String title, String memo,
                Boolean isCompleted, LocalDateTime dueDate, LocalDateTime alarmTime, String recurringRule) {
        this.user = user;
        this.categoryId = categoryId;
        this.parentTodoId = parentTodoId;
        this.title = title;
        this.memo = memo;
        this.isCompleted = (isCompleted != null) ? isCompleted : false; // Default false
        this.dueDate = dueDate;
        this.alarmTime = alarmTime;
        this.recurringRule = recurringRule;
    }

    public void update(Long categoryId, Long parentTodoId, String title, String memo,
                       Boolean isCompleted, LocalDateTime dueDate, LocalDateTime alarmTime, String recurringRule) {
        if (categoryId != null) this.categoryId = categoryId;
        if (parentTodoId != null) this.parentTodoId = parentTodoId;
        if (title != null) this.title = title;
        if (memo != null) this.memo = memo;
        if (isCompleted != null) this.isCompleted = isCompleted;
        if (dueDate != null) this.dueDate = dueDate;
        if (alarmTime != null) this.alarmTime = alarmTime;
        if (recurringRule != null) this.recurringRule = recurringRule;
    }

}
