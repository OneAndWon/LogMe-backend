package com.haru.LogMe.domain.todo.entity;

import com.haru.LogMe.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "todo_category")
public class TodoCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "todo_category_id")
    private Long todoCategoryId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private String name;
    private String color;

    @Builder
    public TodoCategory(User user, String name, String color) {
        this.user = user;
        this.name = name;
        this.color = color;
    }

    // 수정 메서드
    public void update(String name, String color) {
        if (name != null) this.name = name;
        if (color != null) this.color = color;
    }
}
