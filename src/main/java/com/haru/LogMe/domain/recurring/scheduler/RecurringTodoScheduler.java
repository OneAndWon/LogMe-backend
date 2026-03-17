package com.haru.LogMe.domain.recurring.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecurringTodoScheduler {

    /**
     * 매월 1일 새벽 2시에 딱 한 번! 실행되는 스케줄러
     * 목적: 활성화된 반복 규칙 중, 데이터가 11개월 밑으로 떨어진 규칙을 찾아 다시 1년 치를 꽉 채워줍니다.
     */
    // 기존(매일): @Scheduled(cron = "0 0 2 * * *")
    @Scheduled(cron = "0 0 2 1 * *") //매월 1일 02:00:00 에 실행
    public void extendRecurringTodosMonthly() {
        log.info("=== [월간 배치] 반복 일정 연장 스케줄러 작동 시작 ===");

        // TODO (나중에 서비스 안정화 후 구현할 내용):
        // 1. 활성화(ACTIVE) 상태인 RecurringRule 전체 조회
        // 2. 각 규칙으로 생성된 마지막 Todo 날짜가 현재 시점 기준 1년(365일) 이내인지 확인
        // 3. 부족한 개월 수 만큼 TodoService의 날짜 계산 로직을 돌려 Bulk Insert

        log.info("=== [월간 배치] 반복 일정 연장 스케줄러 작동 완료 ===");
    }
}
