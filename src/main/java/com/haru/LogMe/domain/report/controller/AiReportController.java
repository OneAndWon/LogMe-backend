package com.haru.LogMe.domain.report.controller;

import com.haru.LogMe.domain.report.entity.AiReport;
import com.haru.LogMe.domain.report.repository.AiReportRepository;
import com.haru.LogMe.domain.report.service.AiReportService;
import com.haru.LogMe.domain.user.entity.User;
import com.haru.LogMe.global.exception.ErrorCode;
import com.haru.LogMe.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/logme/ai-reports")
@RequiredArgsConstructor
public class AiReportController {

    private final AiReportService aiReportService;
    private final AiReportRepository aiReportRepository;

    @Operation(summary = "AI 리포트 조회", description = "요청 날짜 기준 지난주(월~일) 또는 지난달(1일~말일)의 리포트를 조회/생성합니다.")
    @GetMapping
    public ApiResponse<Map<String, Object>> getAiReport(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @RequestParam String type,
            @RequestParam LocalDate date) {

        // 1. 카톡 기획안 반영: 요청 날짜(date)를 기준으로 지난주/지난달의 정확한 범위 계산
        LocalDate[] dateRange = calculatePastDateRange(type, date);
        LocalDate startDate = dateRange[0];
        LocalDate endDate = dateRange[1];

        // 2. 계산된 시작일과 종료일로 기존 리포트 조회
        Optional<AiReport> existingReport = aiReportRepository
                .findByUserAndTypeAndStartDateAndEndDate(user, type.toUpperCase(), startDate, endDate);

        if (existingReport.isPresent()) {
            AiReport report = existingReport.get();
            Map<String, Object> responseData = new HashMap<>();

            // 과거 데이터는 변하지 않으므로, 완성된 리포트가 있으면 무조건 그대로 반환
            if ("COMPLETED".equals(report.getStatus())) {
                responseData.put("ai_report_id", report.getId());
                responseData.put("status", report.getStatus());
                responseData.put("content", report.getContent());
                return ApiResponse.ok(responseData);

            } else if ("PENDING".equals(report.getStatus())) {
                responseData.put("status", "PENDING");
                responseData.put("message", "AI가 리포트를 생성하고 있습니다. 잠시만 기다려주세요.");
                return ApiResponse.ok(responseData);

            } else { // FAILED
                return ApiResponse.error(ErrorCode.AI_REPORT_GENERATION_FAILED);
            }
        }

        // --- 3. 리포트가 존재하지 않는 경우 새로 생성 ---
        AiReport newReport = AiReport.builder()
                .user(user)
                .type(type.toUpperCase())
                .startDate(startDate)
                .endDate(endDate) // 사용자가 요청한 date가 아닌, 계산된 지난주/지난달의 endDate 사용
                .status("PENDING")
                .build();
        aiReportRepository.save(newReport);

        // 비동기 스레드에 AI 호출 위임 (클라이언트는 안 기다림)
        aiReportService.generateAiReportAsync(newReport.getId(), user, startDate, endDate);

        // 클라이언트에게 즉시 PENDING 응답
        Map<String, Object> pendingData = new HashMap<>();
        pendingData.put("status", "PENDING");
        pendingData.put("message", "AI가 리포트를 생성하고 있습니다. 잠시만 기다려주세요.");

        return ApiResponse.ok(pendingData);
    }

    // 💡 핵심 로직: 캡처본의 요구사항을 완벽히 만족하는 날짜 계산기
    private LocalDate[] calculatePastDateRange(String type, LocalDate requestDate) {
        if ("WEEKLY".equalsIgnoreCase(type)) {
            // 기준일에서 1주일을 뺀 뒤, 그 주의 월요일과 일요일을 구함
            LocalDate pastWeek = requestDate.minusWeeks(1);
            LocalDate start = pastWeek.with(DayOfWeek.MONDAY);
            LocalDate end = pastWeek.with(DayOfWeek.SUNDAY);
            return new LocalDate[]{start, end};

        } else { // MONTHLY
            // 기준일에서 1달을 뺀 뒤, 그 달의 1일과 마지막 날을 구함
            LocalDate pastMonth = requestDate.minusMonths(1);
            LocalDate start = pastMonth.withDayOfMonth(1);
            LocalDate end = pastMonth.withDayOfMonth(pastMonth.lengthOfMonth());
            return new LocalDate[]{start, end};
        }
    }
}
