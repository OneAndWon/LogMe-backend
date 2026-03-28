package com.haru.LogMe.domain.budget.repository;

import com.haru.LogMe.domain.budget.entity.Budget;
import com.haru.LogMe.domain.budget.entity.FinanceCategory;
import com.haru.LogMe.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BudgetRepository extends JpaRepository<Budget, Long> {
    // 월별 예산 목록 조회 (예: 2025-01월 예산 전체), 리포트용
    List<Budget> findAllByUserAndYearMonth(User user, String yearMonth);

    // 예산 중복 생성 방지 및 수정용 조회 (User + Category + YearMonth가 유니크해야 함)
    Optional<Budget> findByUserAndCategoryAndYearMonth(User user, FinanceCategory category, String yearMonth);

    // 예산 삭제/수정 시 ID로 조회 (본인 확인 포함)
    Optional<Budget> findByBudgetIdAndUser(Long budgetId, User user);
}
