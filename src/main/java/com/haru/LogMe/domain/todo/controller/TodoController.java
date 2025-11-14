package com.haru.LogMe.domain.todo.controller;

import com.haru.LogMe.domain.todo.dto.TodoRequest;
import com.haru.LogMe.domain.todo.dto.TodoResponse;
import com.haru.LogMe.domain.todo.service.TodoService;
import com.haru.LogMe.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
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
    public ApiResponse<TodoResponse> createTodo(@RequestBody TodoRequest dto) {
        return ApiResponse.ok(todoService.createTodo(dto));
    }

    // 2. 목록 조회
    @Operation(summary = "할 일 목록 조회", description = "모든 할 일 목록을 조회합니다.")
    @GetMapping
    public ApiResponse<Map<String, Object>> getTodos() {
        List<TodoResponse> list = todoService.getTodos();

        Map<String, Object> data = new HashMap<>();
        data.put("content", list);
        data.put("totalElements", list.size());

        return ApiResponse.ok(data);
    }

    // 3. 수정
    @Operation(summary = "할 일 수정", description = "기존 할 일을 수정합니다.")
    @PatchMapping("/{todoId}")
    public ApiResponse<TodoResponse> updateTodo(@PathVariable Long todoId, @RequestBody TodoRequest dto) {
        // 예외 발생 시 Service에서 throw -> GlobalExceptionHandler가 처리하므로 여기선 ok만 리턴
        return ApiResponse.ok(todoService.updateTodo(todoId, dto));
    }

    // 4. 삭제
    @Operation(summary = "할 일 삭제", description = "기존 할 일을 삭제합니다.")
    @DeleteMapping("/{todoId}")
    public ApiResponse<Map<String, String>> deleteTodo(@PathVariable Long todoId) {
        todoService.deleteTodo(todoId);

        Map<String, String> data = new HashMap<>();
        data.put("message", "할 일이 삭제되었습니다.");

        return ApiResponse.ok(data);
    }
}
