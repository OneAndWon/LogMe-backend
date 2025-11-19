package com.haru.LogMe.domain.budget.repository;

import com.haru.LogMe.domain.budget.entity.FinanceCategory;
import com.haru.LogMe.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface FinanceCategoryRepository extends JpaRepository<FinanceCategory, Long> {
    Optional<FinanceCategory> findByFinanceCategoryIdAndUser(Long categoryId, User user);
}
