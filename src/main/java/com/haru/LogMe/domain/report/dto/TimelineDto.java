package com.haru.LogMe.domain.report.dto;

import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class TimelineDto {
        private final LocalDate date;

        // 일기 데이터
        private String emotion = "없음";
        private String diaryTitle = "없음";
        private String diaryContent = "없음";

        // 투두 데이터
        private int totalTodos = 0;
        private int completedTodos = 0;
        private final List<String> missedHighPriorityTodos = new ArrayList<>();
        private final List<Integer> completedHours = new ArrayList<>();

        // 가계부 데이터
        private int totalIncome = 0;
        private int totalExpense = 0;
        private int totalTransfer = 0;
        private final List<String> expenseDetails = new ArrayList<>();

        public TimelineDto(LocalDate date) {
            this.date = date;
        }

        public void setDiaryInfo(String emotion, String title, String content) {
            this.emotion = emotion != null ? emotion : "없음";
            this.diaryTitle = title != null ? title : "없음";
            if (content != null && content.length() > 500) {
                this.diaryContent = content.substring(0, 500) + "...";
            } else {
                this.diaryContent = content != null ? content : "없음";
            }
        }

        public void addTodoInfo(boolean isCompleted, String priority, String categoryName, String title, LocalDateTime completedAt) {
            this.totalTodos++;
            if (isCompleted) {
                this.completedTodos++;
                if (completedAt != null) {
                    this.completedHours.add(completedAt.getHour()); // 몇 시에 완료했는지 저장
                }
            } else if ("HIGH".equalsIgnoreCase(priority)) {
                this.missedHighPriorityTodos.add(String.format("[%s] %s", categoryName, title));
            }
        }

        public void addFinanceInfo(String type, String categoryName, int amount) {
            if ("INCOME".equalsIgnoreCase(type)) {
                this.totalIncome += amount;
            } else if ("EXPENSE".equalsIgnoreCase(type)) {
                this.totalExpense += amount;
                this.expenseDetails.add(categoryName + "(" + amount + "원)");
            } else if ("TRANSFER".equalsIgnoreCase(type)) {
                this.totalTransfer += amount;
            }
        }
}
