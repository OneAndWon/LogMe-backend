package com.haru.LogMe.domain.recurring.repository;

import com.haru.LogMe.domain.common.BaseTimeEntity;
import com.haru.LogMe.domain.recurring.entity.RecurringRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecurringRuleRepository extends JpaRepository<RecurringRule, Long> {
    // 상태("ACTIVE")와 타겟("TODO")으로 진행 중인 규칙들만 싹 다 가져오기
    List<RecurringRule> findAllByStatusAndTargetType(String status, String targetType);
}
