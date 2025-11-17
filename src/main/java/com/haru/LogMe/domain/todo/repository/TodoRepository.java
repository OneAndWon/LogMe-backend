package com.haru.LogMe.domain.todo.repository;

import com.haru.LogMe.domain.todo.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    List<Todo> findAllByUserIdOrderByDueDateAsc(Long userId);

    Optional<Todo> findByIdAndUserId(Long todoId, Long userId);
}
