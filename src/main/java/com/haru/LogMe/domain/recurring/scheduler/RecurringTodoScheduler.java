package com.haru.LogMe.domain.recurring.scheduler;

import com.haru.LogMe.domain.todo.service.TodoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class RecurringTodoScheduler {

    private final TodoService todoService;

    /**
     * 매월 1일 새벽 2시에 딱 한 번! 실행되는 스케줄러
     * 목적: 활성화된 반복 규칙 중, 데이터가 11개월 밑으로 떨어진 규칙을 찾아 다시 1년 치를 꽉 채워줍니다.
     */
    // 기존(매일): @Scheduled(cron = "0 0 2 * * *")
    @Scheduled(cron = "0 0 2 1 * *") //매월 1일 02:00:00 에 실행
    public void extendRecurringTodosMonthly() {
        log.info("=== [월간 배치] 반복 일정 연장 스케줄러 작동 시작 ===");

        try {
            todoService.extendRecurringTodosMonthly();
            log.info("=== [월간 배치] 반복 일정 연장 처리 성공 ===");
        } catch (Exception e) {
            log.error("=== [월간 배치] 반복 일정 연장 중 오류 발생 ===", e);
        }

        log.info("=== [월간 배치] 반복 일정 연장 스케줄러 작동 완료 ===");
    }
}
