package com.haru.LogMe.domain.todo.repository;

import com.haru.LogMe.domain.todo.entity.TodoCategory;
import com.haru.LogMe.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TodoCategoryRepository extends JpaRepository<TodoCategory, Long> {
    List<TodoCategory> findAllByUser(User user);
    Optional<TodoCategory> findByTodoCategoryIdAndUser(Long todoCategoryId, User user);
}
