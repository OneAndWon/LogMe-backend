package com.haru.LogMe.domain.todo.service;

import com.haru.LogMe.domain.todo.dto.TodoRequest;
import com.haru.LogMe.domain.todo.dto.TodoResponse;
import com.haru.LogMe.domain.todo.entity.Todo;
import com.haru.LogMe.domain.todo.repository.TodoCategoryRepository;
import com.haru.LogMe.domain.todo.repository.TodoRepository;
import com.haru.LogMe.domain.user.entity.User;
import com.haru.LogMe.global.exception.CustomException;
import com.haru.LogMe.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TodoService {
    private final TodoRepository todoRepository;
    private final TodoCategoryRepository todoCategoryRepository;

    // 1. 생성
    @Transactional
    public TodoResponse createTodo(User user, TodoRequest dto) {

        if (!StringUtils.hasText(dto.getTitle())) {
            throw new CustomException(ErrorCode.TODO_TITLE_EMPTY);
        }

        validateCategory(user.getUserId(), dto.getCategoryId());
        validateParentTodo(user, dto.getParentTodoId(), null);

        Todo todo = Todo.builder()
                .user(user) // User 객체 저장
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
    public List<TodoResponse> getTodos(User user) {
        return todoRepository.findAllByUserOrderByDueDateAsc(user).stream()
                .map(TodoResponse::new)
                .collect(Collectors.toList());
    }

    // 3. 수정 (PATCH)
    @Transactional
    public TodoResponse updateTodo(User user, Long todoId, TodoRequest dto) {
        Todo todo = todoRepository.findByIdAndUser(todoId, user)
                .orElseThrow(() -> new CustomException(ErrorCode.TODO_NOT_FOUND));

        validateCategory(user.getUserId(), dto.getCategoryId());
        validateParentTodo(user, dto.getParentTodoId(), todoId);

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
    public void deleteTodo(User user, Long todoId) {
        Todo todo = todoRepository.findByIdAndUser(todoId, user)
                .orElseThrow(() -> new CustomException(ErrorCode.TODO_NOT_FOUND));

        todoRepository.delete(todo);
    }

    //5. 상세 조회
    @Transactional(readOnly = true)
    public TodoResponse getTodoDetail(User user, Long todoId) {
        // 메인 투두 조회 및 검증
        Todo todo = todoRepository.findByIdAndUser(todoId, user)
                .orElseThrow(() -> new CustomException(ErrorCode.TODO_NOT_FOUND));

        // 응답 DTO 생성
        TodoResponse todoResponse = new TodoResponse(todo);

        // 하위 투두 조회 및 변환
        List<TodoResponse> subTodos = todoRepository.findAllByParentTodoId(todoId)
                .stream()
                .map(TodoResponse::new)
                .collect(Collectors.toList());

        //DTO에 하위 투두 설정
        todoResponse.setSubTodos(subTodos);

        return todoResponse;
    }

    /**
     * 카테고리가 존재하며, 해당 유저의 소유인지 검증
     */
    private void validateCategory(Long userId, Long categoryId) {
        if (categoryId == null) return;
        todoCategoryRepository.findByTodoCategoryIdAndUserId(categoryId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    /**
     * 상위 투두가 존재하며, 해당 유저의 소유인지, 그리고 자기 자신이 아닌지 검증
     * @param user 검증할 유저
     * @param parentTodoId 검증할 상위 투두 ID
     * @param currentTodoId 현재 수정 중인 투두 ID (생성 시에는 null)
     */
    private void validateParentTodo(User user, Long parentTodoId, Long currentTodoId) {
        if (parentTodoId == null) {
            return; // 상위 투두 설정 안 함 (유효)
        }

        // (추가) 자기 자신을 부모로 설정하는 것 방지
        if (currentTodoId != null && currentTodoId.equals(parentTodoId)) {
            throw new CustomException(ErrorCode.SELF_PARENT_NOT_ALLOWED);
        }

        // (수정) 상위 투두도 Id와 UserId로 함께 조회 (소유권 검증)
        todoRepository.findByIdAndUser(parentTodoId, user)
                .orElseThrow(() -> new CustomException(ErrorCode.PARENT_TODO_NOT_FOUND));
    }

}
