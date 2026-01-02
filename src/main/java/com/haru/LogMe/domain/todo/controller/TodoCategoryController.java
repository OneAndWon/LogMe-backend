package com.haru.LogMe.domain.todo.controller;

import com.haru.LogMe.domain.todo.dto.TodoCategoryRequest;
import com.haru.LogMe.domain.todo.dto.TodoCategoryResponse;
import com.haru.LogMe.domain.todo.service.TodoCategoryService;
import com.haru.LogMe.domain.user.entity.User;
import com.haru.LogMe.global.response.ApiResponse;
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
@RequestMapping("/logme/todo-categories")
@RequiredArgsConstructor
public class TodoCategoryController {
    private final TodoCategoryService todoCategoryService;

    // 1. 생성
    @Operation(summary = "카테고리 생성")
    @PostMapping
    public ApiResponse<TodoCategoryResponse> createCategory(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody TodoCategoryRequest dto) {
        return ApiResponse.ok(todoCategoryService.createCategory(user, dto));
    }

    // 2. 목록 조회
    @Operation(summary = "카테고리 목록 조회")
    @GetMapping
    public ApiResponse<Map<String, Object>> getCategories(
            @Parameter(hidden = true) @AuthenticationPrincipal User user) {
        List<TodoCategoryResponse> list = todoCategoryService.getCategories(user);

        Map<String, Object> data = new HashMap<>();
        data.put("content", list);
        data.put("totalElements", list.size());

        return ApiResponse.ok(data);
    }

    // 3. 수정
    @Operation(summary = "카테고리 수정")
    @PatchMapping("/{categoryId}")
    public ApiResponse<TodoCategoryResponse> updateCategory(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @PathVariable Long categoryId,
            @RequestBody TodoCategoryRequest dto) {
        return ApiResponse.ok(todoCategoryService.updateCategory(user, categoryId, dto));
    }

    // 4. 삭제
    @Operation(summary = "카테고리 삭제", description = "삭제 시 포함된 할 일들은 미분류로 변경됩니다.")
    @DeleteMapping("/{categoryId}")
    public ApiResponse<Map<String, String>> deleteCategory(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @PathVariable Long categoryId) {
        todoCategoryService.deleteCategory(user, categoryId);

        Map<String, String> data = new HashMap<>();
        data.put("message", "카테고리가 삭제되었습니다.");

        return ApiResponse.ok(data);
    }
}
