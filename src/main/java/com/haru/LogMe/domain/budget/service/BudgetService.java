package com.haru.LogMe.domain.budget.service;

import com.haru.LogMe.domain.budget.dto.BudgetRequest;
import com.haru.LogMe.domain.budget.dto.BudgetResponse;
import com.haru.LogMe.domain.budget.entity.Budget;
import com.haru.LogMe.domain.budget.entity.FinanceCategory;
import com.haru.LogMe.domain.budget.repository.BudgetRepository;
import com.haru.LogMe.domain.budget.repository.FinanceCategoryRepository;
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
@Transactional(readOnly = true)
public class BudgetService {
    private final BudgetRepository budgetRepository;
    private final FinanceCategoryRepository categoryRepository;

    @Transactional
    public BudgetResponse setBudget(User user, BudgetRequest.BudgetSaveDto request) {

        FinanceCategory category = categoryRepository.findByFinanceCategoryIdAndUser(request.getCategoryId(), user)
                .orElseThrow(() -> new CustomException(ErrorCode.FINANCE_CATEGORY_NOT_FOUND));

        Budget budget = budgetRepository.findByUserAndCategoryAndYearMonth(user, category, request.getYearMonth())
                .orElse(null);

        if (budget != null) {
            budget.updateAmount(request.getAmount());
        } else {
            budget = Budget.builder()
                    .user(user)
                    .category(category)
                    .yearMonth(request.getYearMonth())
                    .amount(request.getAmount())
                    .build();
            budgetRepository.save(budget);
        }
        return BudgetResponse.from(budget);
    }

    public ListResponse<BudgetResponse> getBudgets(User user, String yearMonth) {
        List<BudgetResponse> list = budgetRepository.findAllByUserAndYearMonth(user, yearMonth).stream()
                .map(BudgetResponse::from)
                .collect(Collectors.toList());

        return ListResponse.of(list);
    }

    @Transactional
    public void deleteBudget(Long budgetId, User user) {
        Budget budget = budgetRepository.findByBudgetIdAndUser(budgetId, user)
                .orElseThrow(() -> new CustomException(ErrorCode.BUDGET_NOT_FOUND));
        budgetRepository.delete(budget);
    }
}
