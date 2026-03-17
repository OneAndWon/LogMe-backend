package com.haru.LogMe.domain.todo.repository;

import com.haru.LogMe.domain.todo.entity.Todo;
import com.haru.LogMe.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    // User 객체로 조회
    List<Todo> findAllByUserOrderByDueDateAsc(User user);

    // ID와 User 객체로 조회
    Optional<Todo> findByIdAndUser(Long todoId, User user);

    // 부모 할 일 ID로 조회
    List<Todo> findAllByParentTodoId(Long parentTodoId);

    // 카테고리 ID로 조회 - 카테고리 삭제 시 사용
    List<Todo> findAllByCategoryId(Long categoryId);

    // 반복 ID와 시작 날짜로 조회 - 미래 일정 수정/삭제 등 반복 일정 관리 시 사용
    List<Todo> findAllByRecurringIdAndStartDateGreaterThanEqual(Long recurringId, LocalDateTime startDate);
}
