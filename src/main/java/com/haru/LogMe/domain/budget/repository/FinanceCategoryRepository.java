package com.haru.LogMe.domain.budget.repository;

import com.haru.LogMe.domain.budget.entity.FinanceCategory;
import com.haru.LogMe.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FinanceCategoryRepository extends JpaRepository<FinanceCategory, Long> {
    // 본인의 카테고리 목록 전체 조회
    List<FinanceCategory> findAllByUser(User user);

    // 카테고리 수정/삭제 시 본인 확인
    Optional<FinanceCategory> findByFinanceCategoryIdAndUser(Long categoryId, User user);
}
