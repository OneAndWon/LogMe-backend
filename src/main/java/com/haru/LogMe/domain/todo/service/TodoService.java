package com.haru.LogMe.domain.todo.service;

import com.haru.LogMe.domain.recurring.entity.RecurringRule;
import com.haru.LogMe.domain.recurring.repository.RecurringRuleRepository;
import com.haru.LogMe.domain.todo.dto.TodoRequest;
import com.haru.LogMe.domain.todo.dto.TodoResponse;
import com.haru.LogMe.domain.todo.entity.Todo;
import com.haru.LogMe.domain.todo.repository.TodoCategoryRepository;
import com.haru.LogMe.domain.todo.repository.TodoRepository;
import com.haru.LogMe.domain.user.entity.User;
import com.haru.LogMe.global.exception.CustomException;
import com.haru.LogMe.global.exception.ErrorCode;
import com.haru.LogMe.global.response.ListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TodoService {
    private final TodoRepository todoRepository;
    private final TodoCategoryRepository todoCategoryRepository;
    private final RecurringRuleRepository recurringRuleRepository;

    // 1. 생성
    @Transactional
    public TodoResponse createTodo(User user, TodoRequest dto) {
        if (!StringUtils.hasText(dto.getTitle())) {
            throw new CustomException(ErrorCode.TODO_TITLE_EMPTY);
        }
        validateCategory(user, dto.getCategoryId());
        validateParentTodo(user, dto.getParentTodoId(), null);

        // --- 반복 설정이 없는 단건 할 일 ---
        if (dto.getRecurringRule() == null) {
            Todo todo = Todo.builder()
                    .user(user)
                    .categoryId(dto.getCategoryId())
                    .parentTodoId(dto.getParentTodoId())
                    .title(dto.getTitle())
                    .memo(dto.getMemo())
                    .priority(dto.getPriority())
                    .isCompleted(dto.getIsCompleted() != null ? dto.getIsCompleted() : false)
                    .startDate(dto.getStartDate())
                    .dueDate(dto.getDueDate())
                    .alarmTime(dto.getAlarmTime())
                    .build();

            return new TodoResponse(todoRepository.save(todo));
        }

        // --- 반복 설정이 있는 할 일 ---
        // 1. 반복 규칙 뼈대 저장
        RecurringRule rule = RecurringRule.builder()
                .user(user)
                .targetType("TODO")
                .frequencyType(dto.getRecurringRule().getFrequencyType())
                .frequencyValue(dto.getRecurringRule().getFrequencyValue())
                .baseDate(dto.getStartDate().toLocalDate())
                .build();
        recurringRuleRepository.save(rule);

        // 2. 1년 치 미래 데이터 일괄 생성 및 저장
        List<Todo> recurringTodos = generateRecurringTodos(user, dto, rule.getRecurringRuleId());
        todoRepository.saveAll(recurringTodos);

        return new TodoResponse(recurringTodos.get(0));
    }

    // 2. 목록 조회
    @Transactional(readOnly = true)
    public ListResponse<TodoResponse> getTodos(User user) {
        List<TodoResponse> list = todoRepository.findAllByUserOrderByDueDateAsc(user).stream()
                .map(TodoResponse::new)
                .collect(Collectors.toList());

        return ListResponse.of(list);
    }

    // 3. 수정 (PATCH)
    @Transactional
    public TodoResponse updateTodo(User user, Long todoId, TodoRequest dto, String range) {
        Todo todo = todoRepository.findByIdAndUser(todoId, user)
                .orElseThrow(() -> new CustomException(ErrorCode.TODO_NOT_FOUND));

        validateCategory(user, dto.getCategoryId());
        validateParentTodo(user, dto.getParentTodoId(), todoId);

        // 미래 일정 모두 수정할 경우
        if ("future".equals(range) && todo.getRecurringId() != null) {
            List<Todo> futureTodos = todoRepository.findAllByRecurringIdAndStartDateGreaterThanEqual(todo.getRecurringId(), todo.getStartDate());

            for (Todo futureTodo : futureTodos) {
                futureTodo.update(
                        dto.getCategoryId() != null ? dto.getCategoryId() : futureTodo.getCategoryId(),
                        dto.getParentTodoId() != null ? dto.getParentTodoId() : futureTodo.getParentTodoId(),
                        dto.getTitle() != null ? dto.getTitle() : futureTodo.getTitle(),
                        dto.getMemo() != null ? dto.getMemo() : futureTodo.getMemo(),
                        dto.getPriority() != null ? dto.getPriority() : futureTodo.getPriority(),
                        futureTodo.getIsCompleted(), // 미래 일정의 완료 상태는 유지
                        futureTodo.getStartDate(),   // 미래 일정의 시간 유지
                        futureTodo.getDueDate(),
                        futureTodo.getAlarmTime()
                );
            }
        }

        // 타겟 투두(또는 단건) 업데이트
        todo.update(
                dto.getCategoryId(), dto.getParentTodoId(), dto.getTitle(), dto.getMemo(),
                dto.getPriority(), dto.getIsCompleted(), dto.getStartDate(), dto.getDueDate(), dto.getAlarmTime()
        );

        return new TodoResponse(todo);
    }

    // 4. 삭제
    @Transactional
    public Long deleteTodo(User user, Long todoId, String range) {
        Todo todo = todoRepository.findByIdAndUser(todoId, user)
                .orElseThrow(() -> new CustomException(ErrorCode.TODO_NOT_FOUND));

        Long recurringId = todo.getRecurringId();

        if ("future".equals(range) && recurringId != null) {
            RecurringRule rule = recurringRuleRepository.findById(recurringId)
                    .orElseThrow(() -> new CustomException(ErrorCode.RECURRING_RULE_NOT_FOUND));

            rule.stopRecurring(); // 스케줄러 중단 처리

            // 이후 일정 논리 삭제 처리 (@SQLRestriction 적용됨)
            List<Todo> futureTodos = todoRepository.findAllByRecurringIdAndStartDateGreaterThanEqual(recurringId, todo.getStartDate());
            futureTodos.forEach(Todo::delete);

            return recurringId; // 프론트에 반복 ID 반환
        } else {
            // 단건 논리 삭제
            todo.delete();
            return null;
        }
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

    // ==========================================
    // ============= 유틸리티 메서드들 ============
    // ==========================================

    /**
     * 카테고리가 존재하며, 해당 유저의 소유인지 검증
     */
    private void validateCategory(User user, Long categoryId) {
        if (categoryId == null) return;
        todoCategoryRepository.findByTodoCategoryIdAndUser(categoryId, user)
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

    /**
     * 1년 치 반복 투두 생성
     */
    private List<Todo> generateRecurringTodos(User user, TodoRequest dto, Long recurringId) {
        List<Todo> todos = new ArrayList<>();
        LocalDateTime currentStart = dto.getStartDate();
        LocalDateTime currentDue = dto.getDueDate();
        LocalDateTime currentAlarm = dto.getAlarmTime();

        LocalDateTime endLimit = dto.getStartDate().plusYears(1);
        String freqType = dto.getRecurringRule().getFrequencyType();
        int freqValue = dto.getRecurringRule().getFrequencyValue();

        while (!currentStart.isAfter(endLimit)) {
            Todo todo = Todo.builder()
                    .user(user)
                    .categoryId(dto.getCategoryId())
                    .recurringId(recurringId)
                    .parentTodoId(dto.getParentTodoId())
                    .title(dto.getTitle())
                    .memo(dto.getMemo())
                    .priority(dto.getPriority())
                    .isCompleted(false)
                    .startDate(currentStart)
                    .dueDate(currentDue)
                    .alarmTime(currentAlarm)
                    .build();
            todos.add(todo);

            currentStart = calculateNextDate(currentStart, freqType, freqValue);
            if (currentDue != null) currentDue = calculateNextDate(currentDue, freqType, freqValue);
            if (currentAlarm != null) currentAlarm = calculateNextDate(currentAlarm, freqType, freqValue);
        }
        return todos;
    }

    /**
     * 날짜 계산기
     */
    private LocalDateTime calculateNextDate(LocalDateTime date, String type, int value) {
        return switch (type.toUpperCase()) {
            case "DAY" -> date.plusDays(value);
            case "WEEK" -> date.plusWeeks(value);
            case "MONTH" -> date.plusMonths(value);
            default -> throw new CustomException(ErrorCode.UNSUPPORTED_RECURRING_TYPE);
        };
    }

}
