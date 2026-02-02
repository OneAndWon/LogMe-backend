package com.haru.LogMe.domain.budget.controller;

import com.haru.LogMe.domain.budget.dto.BudgetRequest;
import com.haru.LogMe.domain.budget.dto.BudgetResponse;
import com.haru.LogMe.domain.budget.service.BudgetService;
import com.haru.LogMe.domain.user.entity.User;
import com.haru.LogMe.global.response.ApiResponse;
import com.haru.LogMe.global.response.ListResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/logme/budgets")
@RequiredArgsConstructor
public class BudgetController {
    private final BudgetService budgetService;

    // 1. 예산 설정 (없으면 생성, 있으면 수정)
    @PostMapping
    public ApiResponse<BudgetResponse> setBudget(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody BudgetRequest.BudgetSaveDto request) {

        return ApiResponse.ok(budgetService.setBudget(user, request));
    }

    // 2. 월별 예산 목록 조회 (Query Parameter: ?month=2025-11)
    @GetMapping
    public ApiResponse<ListResponse<BudgetResponse>> getBudgets(
            @AuthenticationPrincipal User user,
            @RequestParam("month") String yearMonth) {

        return ApiResponse.ok(budgetService.getBudgets(user, yearMonth));
    }

    // 3. 예산 삭제
    @DeleteMapping("/{budgetId}")
    public ApiResponse<Void> deleteBudget(
            @PathVariable Long budgetId,
            @AuthenticationPrincipal User user) {

        budgetService.deleteBudget(budgetId, user);
        return ApiResponse.ok(null);
    }
}
