package com.haru.LogMe.domain.todo.entity;

import jakarta.persistence.*;
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

    @Column(name = "user_id", nullable = false)
    private Long userId;

    private String name;
    private String color;

    // 생성자
    public TodoCategory(Long userId, String name, String color) {
        this.userId = userId;
        this.name = name;
        this.color = color;
    }
}
