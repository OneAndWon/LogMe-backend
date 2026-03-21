package com.haru.LogMe.domain.budget.repository;

import com.haru.LogMe.domain.budget.entity.Transaction;
import com.haru.LogMe.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    // 특정 유저의 거래내역 전체 조회 (날짜순 정렬 등은 필요 시 추가)
    List<Transaction> findAllByUserOrderByDateDesc(User user);

    // ID와 User로 조회 (보안상 본인 것만 조회/수정/삭제 하기 위함)
    Optional<Transaction> findByTransactionIdAndUser(Long transactionId, User user);

    // 통합 대시보드용: 특정 날짜(하루 범위)의 거래 내역 조회
    List<Transaction> findAllByUserAndDateBetween(User user, LocalDateTime start, LocalDateTime end);
}
