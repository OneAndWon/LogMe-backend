package com.haru.LogMe.domain.report.service;

import com.haru.LogMe.domain.budget.entity.Budget;
import com.haru.LogMe.domain.budget.entity.Transaction;
import com.haru.LogMe.domain.budget.repository.BudgetRepository;
import com.haru.LogMe.domain.budget.repository.TransactionRepository;
import com.haru.LogMe.domain.diary.Repository.DiaryRepository;
import com.haru.LogMe.domain.diary.entity.Diary;
import com.haru.LogMe.domain.report.dto.TimelineDto;
import com.haru.LogMe.domain.todo.entity.Todo;
import com.haru.LogMe.domain.todo.entity.TodoCategory;
import com.haru.LogMe.domain.todo.repository.TodoCategoryRepository;
import com.haru.LogMe.domain.todo.repository.TodoRepository;
import com.haru.LogMe.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Component
@RequiredArgsConstructor
public class ReportDataAggregator {
    private final TodoRepository todoRepository;
     private final TransactionRepository transactionRepository;
     private final DiaryRepository diaryRepository;
     private final BudgetRepository budgetRepository;
     private final TodoCategoryRepository todoCategoryRepository;

    public String aggregateDataForPrompt(User user, LocalDate startDate, LocalDate endDate) {
        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        // 1. 데이터 베이스에서 기간 내 데이터 모두 조회
        List<Todo> todos = todoRepository.findAllByUserAndDueDateBetween(user, startDateTime, endDateTime);
         List<Transaction> transactions = transactionRepository.findAllByUserAndDateBetween(user, startDateTime, endDateTime);
         List<Diary> diaries = diaryRepository.findAllByUserAndDateBetween(user, startDate, endDate);

        // 이번 달 예산 조회 (예: '2026-03')
        String yearMonth = startDate.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        List<Budget> budgets = budgetRepository.findAllByUserAndYearMonth(user, yearMonth);

        // 유저의 모든 투두 카테고리를 한 번에 조회하여 Map으로 캐싱 (N+1 문제 완벽 차단)
        List<TodoCategory> categories = todoCategoryRepository.findAllByUser(user);
        Map<Long, String> categoryMap = new HashMap<>();
        for (TodoCategory category : categories) {
            categoryMap.put(category.getTodoCategoryId(), category.getName());
        }

        // 2. 날짜별로 데이터를 담을 Map 초기화 (TreeMap을 사용하여 날짜순 정렬 보장)
        Map<LocalDate, TimelineDto> timelineMap = new TreeMap<>();
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            timelineMap.put(date, new TimelineDto(date));
        }

        // 3. 투두 데이터 분류
        for (Todo todo : todos) {
            if (todo.getDueDate() != null) {
                LocalDate date = todo.getDueDate().toLocalDate();
                if (timelineMap.containsKey(date)) {
                    String priorityStr = todo.getPriority() != null ? todo.getPriority().name() : "MEDIUM";

                    // [핵심 추가] 매번 DB를 찌르지 않고 미리 만들어둔 Map에서 카테고리 이름 가져오기
                    String categoryName = "미분류"; // 사용자가 카테고리를 지정하지 않았을 때의 방어 로직
                    if (todo.getCategoryId() != null && categoryMap.containsKey(todo.getCategoryId())) {
                        categoryName = categoryMap.get(todo.getCategoryId());
                    }

                    // TimelineDto에 카테고리 이름(categoryName)을 파라미터로 추가 전달
                    timelineMap.get(date).addTodoInfo(
                            todo.getIsCompleted(),
                            priorityStr,
                            categoryName,
                            todo.getTitle(),
                            todo.getCompletedAt()
                    );
                }
            }
        }

        // 4. 가계부 데이터 분류
        for (Transaction tx : transactions) {
            LocalDate date = tx.getDate().toLocalDate();
            if (timelineMap.containsKey(date)) {
                String catName = tx.getCategory() != null ? tx.getCategory().getName() : "미분류";
                timelineMap.get(date).addFinanceInfo(tx.getType().name(), catName, tx.getAmount().intValue());
            }
        }

        // 5. 일기 데이터 분류
        for (Diary diary : diaries) {
            LocalDate date = diary.getDate();
            if (timelineMap.containsKey(date)) {
                timelineMap.get(date).setDiaryInfo(diary.getEmotionIcon().name(), diary.getTitle(), diary.getContent());
            }
        }

        // 6. AI에게 전달할 최종 텍스트(Prompt) 조립
        StringBuilder prompt = new StringBuilder();
        prompt.append("[분석 기간]: ").append(startDate).append(" ~ ").append(endDate).append("\n");

        if (!budgets.isEmpty()) {
            prompt.append("[이번 달 예산 설정 내역]\n");
            int totalBudget = 0;
            for (Budget budget : budgets) {
                String catName = budget.getCategory() != null ? budget.getCategory().getName() : "미분류";
                int amt = budget.getAmount().intValue();
                totalBudget += amt;
                prompt.append("- ").append(catName).append(": ").append(amt).append("원\n");
            }
            prompt.append("▶ 총 예산: ").append(totalBudget).append("원\n\n");
        }

        prompt.append("아래는 사용자의 일자별 감정, 생산성(할 일), 재정(현금 흐름) 데이터입니다. 이를 융합하여 입체적인 패턴을 분석해주세요.\n\n");

        for (Map.Entry<LocalDate, TimelineDto> entry : timelineMap.entrySet()) {
            TimelineDto dto = entry.getValue();
            prompt.append("=== ").append(dto.getDate()).append(" ===\n");

            // 일기 파트
            prompt.append("[일기] 감정: ").append(dto.getEmotion()).append(" | 제목: ").append(dto.getDiaryTitle()).append("\n");
            prompt.append("내용 요약: ").append(dto.getDiaryContent()).append("\n");

            // 투두 파트
            int todoPercent = dto.getTotalTodos() > 0 ? (int) ((double) dto.getCompletedTodos() / dto.getTotalTodos() * 100) : 0;
            prompt.append("[투두] 총 ").append(dto.getTotalTodos()).append("개 중 ").append(dto.getCompletedTodos()).append("개 완료 (달성률 ").append(todoPercent).append("%)\n");
            if (!dto.getMissedHighPriorityTodos().isEmpty()) {
                prompt.append("미룬 중요 항목(HIGH): ").append(String.join(", ", dto.getMissedHighPriorityTodos())).append("\n");
            }

            // 가계부 파트
            prompt.append("[재정] 수입: ").append(dto.getTotalIncome()).append("원 | 지출: ").append(dto.getTotalExpense()).append("원 | 저축/이체: ").append(dto.getTotalTransfer()).append("원\n");
            if (!dto.getExpenseDetails().isEmpty()) {
                prompt.append("지출 내역: ").append(String.join(", ", dto.getExpenseDetails())).append("\n");
            }
            prompt.append("\n");
        }

        return prompt.toString();
    }
}
