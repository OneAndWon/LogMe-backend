package com.haru.LogMe.domain.todo.repository;

import com.haru.LogMe.domain.todo.entity.Todo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    // MVP: 모든 목록 조회 (추후 userId 조건 추가 필요)
    List<Todo> findAllByOrderByDueDateAsc();
}
