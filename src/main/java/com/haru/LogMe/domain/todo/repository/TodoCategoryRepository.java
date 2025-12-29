package com.haru.LogMe.domain.todo.repository;

import com.haru.LogMe.domain.todo.entity.TodoCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TodoCategoryRepository extends JpaRepository<TodoCategory, Long> {
    Optional<TodoCategory> findByTodoCategoryIdAndUserId(Long todoCategoryId, Long userId);
}
