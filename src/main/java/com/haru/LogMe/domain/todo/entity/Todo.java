package com.haru.LogMe.domain.todo.entity;

import com.haru.LogMe.domain.common.BaseTimeEntity;
import com.haru.LogMe.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "todo")
@SQLRestriction("deleted_at IS NULL")
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

    @Column(name = "recurring_id")
    private Long recurringId;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority")
    private TodoPriority priority; // ('HIGH', 'MEDIUM', 'LOW')

    @Column(name = "is_completed")
    private Boolean isCompleted;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "alarm_time")
    private LocalDateTime alarmTime; // 알림 시간

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Builder
    public Todo(User user, Long categoryId, Long recurringId, Long parentTodoId, String title, String memo,
                TodoPriority priority, Boolean isCompleted, LocalDateTime completedAt, LocalDateTime startDate, LocalDateTime dueDate, LocalDateTime alarmTime) {
        this.user = user;
        this.categoryId = categoryId;
        this.recurringId = recurringId;
        this.parentTodoId = parentTodoId;
        this.title = title;
        this.memo = memo;
        this.priority = (priority != null) ? priority : TodoPriority.MEDIUM;
        this.isCompleted = (isCompleted != null) ? isCompleted : false;
        this.completedAt = completedAt;
        this.startDate = startDate;
        this.dueDate = dueDate;
        this.alarmTime = alarmTime;
    }

    public void update(Long categoryId, Long parentTodoId, String title, String memo,
                       TodoPriority priority, Boolean isCompleted, LocalDateTime startDate,
                       LocalDateTime dueDate, LocalDateTime alarmTime) {
        if (isCompleted != null) {
            // false -> true로 변할 때만 현재 시간 기록
            if (isCompleted && (this.isCompleted == null || !this.isCompleted)) {
                this.completedAt = LocalDateTime.now();
            }
            // true -> false로 변할 때 시간 초기화
            else if (!isCompleted) {
                this.completedAt = null;
            }
            this.isCompleted = isCompleted;
        }

        if (categoryId != null) this.categoryId = categoryId;
        if (parentTodoId != null) this.parentTodoId = parentTodoId;
        if (title != null) this.title = title;
        if (memo != null) this.memo = memo;
        if (priority != null) this.priority = priority;
        if (isCompleted != null) this.isCompleted = isCompleted;
        if (startDate != null) this.startDate = startDate;
        if (dueDate != null) this.dueDate = dueDate;
        if (alarmTime != null) this.alarmTime = alarmTime;
    }

    public void delete() {
        this.deletedAt = LocalDateTime.now();
    }

    public void removeCategory() {
        this.categoryId = null;
    }
}
