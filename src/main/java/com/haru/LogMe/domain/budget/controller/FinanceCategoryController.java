package com.haru.LogMe.domain.budget.controller;

import com.haru.LogMe.domain.budget.dto.FinanceCategoryRequest;
import com.haru.LogMe.domain.budget.dto.FinanceCategoryResponse;
import com.haru.LogMe.domain.budget.service.FinanceCategoryService;
import com.haru.LogMe.domain.user.entity.User;
import com.haru.LogMe.global.response.ApiResponse;
import com.haru.LogMe.global.response.ListResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/logme/finance-categories")
@RequiredArgsConstructor
public class FinanceCategoryController {
    private final FinanceCategoryService categoryService;

    // 1. 생성
    @PostMapping
    public ApiResponse<FinanceCategoryResponse> createCategory(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody FinanceCategoryRequest.FinanceCategoryCreateDto request) {

        return ApiResponse.ok(categoryService.createCategory(user, request));
    }

    // 2. 목록 조회 (Query Parameter: ?type=EXPENSE)
    @GetMapping
    public ApiResponse<ListResponse<FinanceCategoryResponse>> getCategories(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) String type) {

        return ApiResponse.ok(categoryService.getCategories(user, type));
    }

    // 3. 수정
    @PutMapping("/{categoryId}")
    public ApiResponse<FinanceCategoryResponse> updateCategory(
            @PathVariable Long categoryId,
            @AuthenticationPrincipal User user,
            @RequestBody FinanceCategoryRequest.FinanceCategoryUpdateDto request) {

        return ApiResponse.ok(categoryService.updateCategory(categoryId, user, request));
    }

    // 4. 삭제
    @DeleteMapping("/{categoryId}")
    public ApiResponse<Void> deleteCategory(
            @PathVariable Long categoryId,
            @AuthenticationPrincipal User user) {

        categoryService.deleteCategory(categoryId, user);
        return ApiResponse.ok(null);
    }
}
