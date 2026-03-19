package com.haru.LogMe.domain.todo.controller;

import com.haru.LogMe.domain.todo.dto.TodoRequest;
import com.haru.LogMe.domain.todo.dto.TodoResponse;
import com.haru.LogMe.domain.todo.service.TodoService;
import com.haru.LogMe.domain.user.entity.User;
import com.haru.LogMe.global.response.ApiResponse;
import com.haru.LogMe.global.response.ListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/logme/todos")
@RequiredArgsConstructor
public class TodoController {
    private final TodoService todoService;

    // 1. 생성
    @Operation(summary = "할 일 생성", description = "새로운 할 일을 생성합니다.")
    @PostMapping
    public ApiResponse<TodoResponse> createTodo(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody TodoRequest dto) {
        return ApiResponse.ok(todoService.createTodo(user,dto));
    }

    // 2. 목록 조회
    @Operation(summary = "할 일 목록 조회", description = "모든 할 일 목록을 조회합니다.")
    @GetMapping
    public ApiResponse<ListResponse<TodoResponse>> getTodos(
            @Parameter(hidden = true) @AuthenticationPrincipal User user
    ) {
        return ApiResponse.ok(todoService.getTodos(user));
    }

    // 3. 수정
    @Operation(summary = "할 일 수정", description = "기존 할 일을 수정합니다.")
    @PatchMapping("/{todoId}")
    public ApiResponse<TodoResponse> updateTodo(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @PathVariable Long todoId,
            @RequestParam(required = false) String range,
            @Valid @RequestBody TodoRequest dto
    ) {
        return ApiResponse.ok(todoService.updateTodo(user, todoId, dto, range));
    }

    // 4. 삭제
    @Operation(summary = "할 일 삭제", description = "기존 할 일을 삭제합니다.")
    @DeleteMapping("/{todoId}")
    public ApiResponse<Map<String, Object>> deleteTodo(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @PathVariable Long todoId,
            @RequestParam(required = false) String range
    ) {
        Long deletedRecurringId = todoService.deleteTodo(user, todoId, range);

        Map<String, Object> data = new HashMap<>();
        data.put("deleted_todo_id", todoId);
        data.put("deleted_recurring_id", deletedRecurringId);
        data.put("message", "future".equals(range) ? "선택한 할 일과 이후 반복 일정이 모두 삭제되었습니다." : "할 일이 삭제되었습니다.");

        return ApiResponse.ok(data);
    }

    // 5. 상세 조회
    @Operation(summary = "할 일 상세 조회", description = "특정 할 일의 상세 정보를 조회합니다.")
    @GetMapping("/{todoId}")
    public ApiResponse<TodoResponse> getTodoDetail(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @PathVariable Long todoId
    ) {
        return ApiResponse.ok(todoService.getTodoDetail(user, todoId));
    }

    // === 개발 및 관리자 테스트용 ===
    @Operation(summary = "[테스트용] 월간 반복 일정 연장 수동 트리거")
    @PostMapping("/test/extend-recurring")
    public ApiResponse<String> testExtendRecurringTodos() {
        todoService.extendRecurringTodosMonthly(); // 스케줄러가 부르던 서비스 로직을 직접 호출!
        return ApiResponse.ok("반복 일정 연장 배치가 수동으로 완료되었습니다.");
    }
}
