package com.haru.LogMe.domain.todo.service;

import com.haru.LogMe.domain.todo.dto.TodoRequest;
import com.haru.LogMe.domain.todo.dto.TodoResponse;
import com.haru.LogMe.domain.todo.entity.Todo;
import com.haru.LogMe.domain.todo.repository.TodoCategoryRepository;
import com.haru.LogMe.domain.todo.repository.TodoRepository;
import com.haru.LogMe.global.exception.CustomException;
import com.haru.LogMe.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TodoService {
    private final TodoRepository todoRepository;
    private final TodoCategoryRepository todoCategoryRepository;

    // 1. 생성
    @Transactional
    public TodoResponse createTodo(Long userId, TodoRequest dto) {
        // 카테고리 유효성 및 소유권 검사
        validateCategory(userId, dto.getCategoryId());
        // 상위 투두 유효성 및 소유권 검사 (생성 시 currentTodoId는 null)
        validateParentTodo(userId, dto.getParentTodoId(), null);

        Todo todo = Todo.builder()
                .userId(userId) // (수정)
                .categoryId(dto.getCategoryId())
                .parentTodoId(dto.getParentTodoId())
                .title(dto.getTitle())
                .memo(dto.getMemo())
                .dueDate(dto.getDueDate())
                .alarmTime(dto.getAlarmTime())
                .recurringRule(dto.getRecurringRule())
                .build();

        return new TodoResponse(todoRepository.save(todo));
    }

    // 2. 목록 조회
    @Transactional(readOnly = true)
    public List<TodoResponse> getTodos(Long userId) {
        //userId로 필터링
        return todoRepository.findAllByUserIdOrderByDueDateAsc(userId).stream()
                .map(TodoResponse::new)
                .collect(Collectors.toList());
    }

    // 3. 수정 (PATCH)
    @Transactional
    public TodoResponse updateTodo(Long userId, Long todoId, TodoRequest dto) {
        // findById 대신, ID와 UserId로 한 번에 조회 (소유권 검증)
        Todo todo = todoRepository.findByIdAndUserId(todoId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.TODO_NOT_FOUND));

        // 변경하려는 카테고리 유효성 및 소유권 검사
        validateCategory(userId, dto.getCategoryId());
        //변경하려는 상위 투두 유효성 및 소유권 검사 (자기 자신 체크)
        validateParentTodo(userId, dto.getParentTodoId(), todoId);

        todo.update(
                dto.getCategoryId(),
                dto.getParentTodoId(),
                dto.getTitle(),
                dto.getMemo(),
                dto.getIsCompleted(),
                dto.getDueDate(),
                dto.getAlarmTime(),
                dto.getRecurringRule()
        );

        return new TodoResponse(todo);
    }

    // 4. 삭제
    @Transactional
    public void deleteTodo(Long userId, Long todoId) { // (수정)
        // findByIdAndUserId로 소유권까지 한 번에 확인
        Todo todo = todoRepository.findByIdAndUserId(todoId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.TODO_NOT_FOUND));

        todoRepository.delete(todo);
    }

    /**
     * 카테고리가 존재하며, 해당 유저의 소유인지 검증
     */
    private void validateCategory(Long userId, Long categoryId) {
        if (categoryId == null) {
            return; // 카테고리 설정 안 함 (유효)
        }

        // 카테고리도 Id와 UserId로 함께 조회 (소유권 검증)
        todoCategoryRepository.findByIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    /**
     * 상위 투두가 존재하며, 해당 유저의 소유인지, 그리고 자기 자신이 아닌지 검증
     * @param userId 현재 유저 ID
     * @param parentTodoId 검증할 상위 투두 ID
     * @param currentTodoId 현재 수정 중인 투두 ID (생성 시에는 null)
     */
    private void validateParentTodo(Long userId, Long parentTodoId, Long currentTodoId) {
        if (parentTodoId == null) {
            return; // 상위 투두 설정 안 함 (유효)
        }

        // (추가) 자기 자신을 부모로 설정하는 것 방지
        if (currentTodoId != null && currentTodoId.equals(parentTodoId)) {
            throw new CustomException(ErrorCode.SELF_PARENT_NOT_ALLOWED);
        }

        // (수정) 상위 투두도 Id와 UserId로 함께 조회 (소유권 검증)
        todoRepository.findByIdAndUserId(parentTodoId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.PARENT_TODO_NOT_FOUND));
    }

}
