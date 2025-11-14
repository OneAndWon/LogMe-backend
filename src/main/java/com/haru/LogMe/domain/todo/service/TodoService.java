package com.haru.LogMe.domain.todo.service;

import com.haru.LogMe.domain.todo.dto.TodoRequest;
import com.haru.LogMe.domain.todo.dto.TodoResponse;
import com.haru.LogMe.domain.todo.entity.Todo;
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

    // 1. 생성
    @Transactional
    public TodoResponse createTodo(TodoRequest dto) {
        Todo todo = Todo.builder()
                .userId(1L) // 임시 사용자 ID
                .categoryId(dto.getCategoryId())
                .parentTodoId(dto.getParentTodoId()) // 상위 투두 ID 저장
                .title(dto.getTitle())
                .memo(dto.getMemo())
                .dueDate(dto.getDueDate())
                .alarmTime(dto.getAlarmTime())       // 알람 시간 저장
                .recurringRule(dto.getRecurringRule()) // 반복 규칙 저장
                .build();

        return new TodoResponse(todoRepository.save(todo));
    }

    // 2. 목록 조회
    @Transactional(readOnly = true)
    public List<TodoResponse> getTodos() {
        return todoRepository.findAllByOrderByDueDateAsc().stream()
                .map(TodoResponse::new)
                .collect(Collectors.toList());
    }

    // 3. 수정 (PATCH)
    @Transactional
    public TodoResponse updateTodo(Long todoId, TodoRequest dto) {
        Todo todo = todoRepository.findById(todoId)
                .orElseThrow(() -> new CustomException(ErrorCode.TODO_NOT_FOUND));

        // 모든 필드 업데이트 (null이 아닌 값만)
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
    public void deleteTodo(Long todoId) {
        if (!todoRepository.existsById(todoId)) {
            throw new CustomException(ErrorCode.TODO_NOT_FOUND);
        }
        todoRepository.deleteById(todoId);
    }

}
