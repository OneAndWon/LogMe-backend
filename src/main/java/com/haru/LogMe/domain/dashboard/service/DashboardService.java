package com.haru.LogMe.domain.dashboard.service;

import com.haru.LogMe.domain.budget.entity.Transaction;
import com.haru.LogMe.domain.budget.entity.TransactionType;
import com.haru.LogMe.domain.budget.repository.TransactionRepository;
import com.haru.LogMe.domain.dashboard.dto.DashboardResponse;
import com.haru.LogMe.domain.dashboard.dto.MottoRequest;
import com.haru.LogMe.domain.dashboard.dto.MottoResponse;
import com.haru.LogMe.domain.dashboard.entity.DailySummary;
import com.haru.LogMe.domain.dashboard.repository.DailySummaryRepository;
import com.haru.LogMe.domain.diary.Repository.DiaryRepository;
import com.haru.LogMe.domain.diary.entity.Diary;
import com.haru.LogMe.domain.todo.entity.Todo;
import com.haru.LogMe.domain.todo.entity.TodoCategory;
import com.haru.LogMe.domain.todo.repository.TodoCategoryRepository;
import com.haru.LogMe.domain.todo.repository.TodoRepository;
import com.haru.LogMe.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {
    private final DailySummaryRepository dailySummaryRepository;

    // 타 도메인 데이터 조회를 위한 Repository 직접 주입
    private final TodoRepository todoRepository;
    private final TodoCategoryRepository todoCategoryRepository;
    private final DiaryRepository diaryRepository;
    private final TransactionRepository transactionRepository;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    @Transactional
    public MottoResponse upsertMotto(User user, MottoRequest request) {
        DailySummary summary = dailySummaryRepository.findByUserAndDate(user, request.getDate())
                .orElseGet(() -> DailySummary.builder()
                        .user(user)
                        .date(request.getDate())
                        .motto(request.getMotto())
                        .build());

        if (summary.getId() != null) {
            summary.updateMotto(request.getMotto());
        } else {
            dailySummaryRepository.save(summary);
        }

        return new MottoResponse(summary);
    }

    public DashboardResponse getDashboard(User user, LocalDate date) {

        // 하루의 시작과 끝 시간 계산
        LocalDateTime startOfDay = date.atStartOfDay();
        LocalDateTime endOfDay = date.atTime(LocalTime.MAX);

        // 1. 오늘의 다짐
        String motto = dailySummaryRepository.findByUserAndDate(user, date)
                .map(DailySummary::getMotto)
                .orElse(null);

        List<DashboardResponse.TimelineItemDto> timeline = new ArrayList<>();

        // ==========================================
        // 2. [Todo] 집계 및 타임라인 (예정된 할 일, 카테고리 이름 포함)
        // ==========================================
        List<Todo> dailyTodos = todoRepository.findAllByUserAndDueDateBetween(user, startOfDay, endOfDay);

        // 카테고리 조회를 위한 N+1 방지용 메모리 Map 캐싱
        Map<Long, String> categoryMap = todoCategoryRepository.findAllByUser(user).stream()
                .collect(Collectors.toMap(TodoCategory::getTodoCategoryId, TodoCategory::getName));

        long todoTotalCount = dailyTodos.size();
        long todoCompletedCount = dailyTodos.stream().filter(Todo::getIsCompleted).count();

        // 예정된 할 일 (미완료 항목 최대 2개 추출)
        List<DashboardResponse.UpcomingTodoDto> upcomingTodos = dailyTodos.stream()
                .filter(todo -> !todo.getIsCompleted())
                .map(todo -> DashboardResponse.UpcomingTodoDto.builder()
                        .time(todo.getDueDate() != null ? todo.getDueDate().format(TIME_FORMATTER) : "")
                        .title(todo.getTitle())
                        .build())
                .limit(2)
                .collect(Collectors.toList());

        for (Todo todo : dailyTodos) {
            String timeStr = (todo.getDueDate() != null) ? todo.getDueDate().format(TIME_FORMATTER) : "00:00";
            // 카테고리 맵에서 이름 꺼내오기
            String categoryName = (todo.getCategoryId() != null) ? categoryMap.get(todo.getCategoryId()) : null;

            timeline.add(DashboardResponse.TimelineItemDto.builder()
                    .type("TODO")
                    .time(timeStr)
                    .title(todo.getTitle())
                    .isCompleted(todo.getIsCompleted())
                    .categoryName(categoryName) // 카테고리 이름 세팅
                    .build());
        }

        // ==========================================
        // 3. [Diary] 집계 (본문 미리보기 포함)
        // ==========================================
        Optional<Diary> dailyDiary = diaryRepository.findByUserAndDate(user, date);
        boolean hasDiary = dailyDiary.isPresent();
        String emotionIcon = dailyDiary.map(Diary::getEmotionIcon).orElse(null);

        // 본문 텍스트가 20자를 넘어가면 "..." 처리
        String contentPreview = dailyDiary.map(Diary::getContent) // (Diary 엔티티의 본문 Getter 메서드명 확인 필요: getContent() 또는 getContentText())
                .map(content -> content.length() > 20 ? content.substring(0, 20) + "..." : content)
                .orElse(null);

        // 일기가 존재하면 작성 시간(createdAt)을 기준으로 타임라인에 추가
        dailyDiary.ifPresent(diary -> {
            String diaryTimeStr = (diary.getCreatedAt() != null) ? diary.getCreatedAt().format(TIME_FORMATTER) : "00:00";

            timeline.add(DashboardResponse.TimelineItemDto.builder()
                    .type("DIARY")
                    .time(diaryTimeStr)
                    .title(diary.getTitle())
                    .build());
        });

        // ==========================================
        // 4. [Budget] 집계 및 타임라인
        // ==========================================
        List<Transaction> dailyTransactions = transactionRepository.findAllByUserAndDateBetween(user, startOfDay, endOfDay);
        long totalIncome = 0L;
        long totalExpense = 0L;

        for (Transaction tx : dailyTransactions) {
            long amount = tx.getAmount().longValue();

            // 🟢 문자열 비교가 아닌 Enum 비교(==)로 변경!
            if (TransactionType.INCOME == tx.getType()) {
                totalIncome += amount;
                timeline.add(DashboardResponse.TimelineItemDto.builder()
                        .type("TRANSACTION")
                        .time(tx.getDate().format(TIME_FORMATTER))
                        .title(tx.getDescription())
                        .amount(amount)
                        .build());
            } else if (TransactionType.EXPENSE == tx.getType()) {
                totalExpense += amount;
                timeline.add(DashboardResponse.TimelineItemDto.builder()
                        .type("TRANSACTION")
                        .time(tx.getDate().format(TIME_FORMATTER))
                        .title(tx.getDescription())
                        .amount(-amount) // 지출은 음수로 반환
                        .build());
            }
        }

        // 5. 타임라인 시간순 정렬
        timeline.sort(Comparator.comparing(DashboardResponse.TimelineItemDto::getTime));

        // 6. 최종 객체 조립 및 반환
        return DashboardResponse.builder()
                .date(date)
                .motto(motto)
                .todoSummary(DashboardResponse.TodoSummaryDto.builder()
                        .totalCount(todoTotalCount)
                        .completedCount(todoCompletedCount)
                        .upcomingTodos(upcomingTodos)
                        .build())
                .diarySummary(DashboardResponse.DiarySummaryDto.builder()
                        .hasDiary(hasDiary)
                        .emotionIcon(emotionIcon)
                        .contentPreview(contentPreview)
                        .build())
                .budgetSummary(DashboardResponse.BudgetSummaryDto.builder()
                        .totalExpense(totalExpense)
                        .totalIncome(totalIncome)
                        .build())
                .timeline(timeline)
                .build();
    }
}
