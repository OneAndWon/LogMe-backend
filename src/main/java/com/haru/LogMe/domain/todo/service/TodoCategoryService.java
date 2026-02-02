package com.haru.LogMe.domain.todo.service;

import com.haru.LogMe.domain.todo.dto.TodoCategoryRequest;
import com.haru.LogMe.domain.todo.dto.TodoCategoryResponse;
import com.haru.LogMe.domain.todo.entity.Todo;
import com.haru.LogMe.domain.todo.entity.TodoCategory;
import com.haru.LogMe.domain.todo.repository.TodoCategoryRepository;
import com.haru.LogMe.domain.todo.repository.TodoRepository;
import com.haru.LogMe.domain.user.entity.User;
import com.haru.LogMe.global.exception.CustomException;
import com.haru.LogMe.global.exception.ErrorCode;
import com.haru.LogMe.global.response.ListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TodoCategoryService {
    private final TodoCategoryRepository todoCategoryRepository;
    private final TodoRepository todoRepository;

    // 1. 생성
    @Transactional
    public TodoCategoryResponse createCategory(User user, TodoCategoryRequest dto) {
        TodoCategory category = TodoCategory.builder()
                .user(user)
                .name(dto.getName())
                .color(dto.getColor())
                .build();
        return new TodoCategoryResponse(todoCategoryRepository.save(category));
    }

    // 2. 목록 조회
    @Transactional(readOnly = true)
    public ListResponse<TodoCategoryResponse> getCategories(User user) {
        List<TodoCategoryResponse> list = todoCategoryRepository.findAllByUser(user).stream()
                .map(TodoCategoryResponse::new)
                .collect(Collectors.toList());

        return ListResponse.of(list);
    }

    // 3. 수정
    @Transactional
    public TodoCategoryResponse updateCategory(User user, Long categoryId, TodoCategoryRequest dto) {
        TodoCategory category = todoCategoryRepository.findByTodoCategoryIdAndUser(categoryId, user)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        category.update(dto.getName(), dto.getColor());
        return new TodoCategoryResponse(category);
    }

    // 4. 삭제
    @Transactional
    public void deleteCategory(User user, Long categoryId) {
        TodoCategory category = todoCategoryRepository.findByTodoCategoryIdAndUser(categoryId, user)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));

        // 삭제 전, 연결된 투두들의 카테고리를 null로 변경 (미분류 처리)
        List<Todo> relatedTodos = todoRepository.findAllByCategoryId(categoryId);
        for (Todo todo : relatedTodos) {
            todo.removeCategory();
        }

        todoCategoryRepository.delete(category);
    }
}
