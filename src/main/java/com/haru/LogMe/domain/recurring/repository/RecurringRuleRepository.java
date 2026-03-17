package com.haru.LogMe.domain.recurring.repository;

import com.haru.LogMe.domain.common.BaseTimeEntity;
import com.haru.LogMe.domain.recurring.entity.RecurringRule;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecurringRuleRepository extends JpaRepository<RecurringRule, Long> {

}
