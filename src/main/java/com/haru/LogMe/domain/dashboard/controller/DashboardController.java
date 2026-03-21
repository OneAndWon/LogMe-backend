package com.haru.LogMe.domain.dashboard.controller;

import com.haru.LogMe.domain.dashboard.dto.DashboardResponse;
import com.haru.LogMe.domain.dashboard.dto.MottoRequest;
import com.haru.LogMe.domain.dashboard.dto.MottoResponse;
import com.haru.LogMe.domain.dashboard.service.DashboardService;
import com.haru.LogMe.domain.user.entity.User;
import com.haru.LogMe.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Tag(name = "Dashboard", description = "통합 대시보드 API")
@RestController
@RequestMapping("/logme")
@RequiredArgsConstructor
public class DashboardController {
    private final DashboardService dashboardService;

    @Operation(summary = "오늘의 다짐 작성 및 수정 (Upsert)")
    @PostMapping("/daily-summary/motto")
    public ApiResponse<MottoResponse> upsertMotto(
            @Parameter(hidden = true) @AuthenticationPrincipal User user,
            @Valid @RequestBody MottoRequest request
    ) {
        // 마찬가지로 User 객체를 통째로 넘깁니다.
        MottoResponse response = dashboardService.upsertMotto(user, request);
        return ApiResponse.ok(response);
    }

    @Operation(summary = "통합 대시보드 조회", description = "특정 날짜의 요약 및 타임라인을 병합하여 반환합니다.")
    @GetMapping("/dashboard")
    public ApiResponse<DashboardResponse> getDashboard(
            @Parameter(hidden = true) @AuthenticationPrincipal User user, // Swagger에서 User 객체 입력창 숨기기
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date
    ) {
        // userId를 꺼내지 않고, User 객체를 통째로 서비스에 넘깁니다.
        DashboardResponse response = dashboardService.getDashboard(user, date);
        return ApiResponse.ok(response);
    }
}
