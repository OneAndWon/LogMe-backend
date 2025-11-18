package com.haru.LogMe.domain.diary.controller;

import com.haru.LogMe.domain.diary.dto.DiaryRequest;
import com.haru.LogMe.domain.diary.dto.DiaryResponse;
import com.haru.LogMe.domain.diary.service.DiaryService;
import com.haru.LogMe.domain.user.entity.User;
import com.haru.LogMe.global.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/logme/diaries")
@RequiredArgsConstructor
public class DiaryController {
    private final DiaryService diaryService;

    // 1. Upsert
    @Operation(summary = "일기 저장 (Upsert)", description = "해당 날짜에 일기가 없으면 생성, 있으면 수정합니다.")
    @PostMapping
    public ApiResponse<DiaryResponse.Detail> upsertDiary(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody DiaryRequest dto) {
        return ApiResponse.ok(diaryService.upsertDiary(user.getUserId(), dto));
    }

    // 2. 목록 조회 (수정됨)
    @Operation(summary = "일기 목록 조회 (캘린더용)")
    @GetMapping
    public ApiResponse<Map<String, Object>> getDiaries(
            @AuthenticationPrincipal User user,
            @RequestParam(name = "year-month") String yearMonth // "YYYY-MM"
    ) {
        // 서비스에서 parsing 수행
        List<DiaryResponse.Summary> list = diaryService.getDiaries(user.getUserId(), yearMonth);

        Map<String, Object> data = new HashMap<>();
        data.put("content", list);
        data.put("totalElements", list.size());

        return ApiResponse.ok(data);
    }

    // 3. 상세 조회
    @Operation(summary = "일기 상세 조회", description = "특정 날짜의 일기 상세 정보를 조회합니다.")
    @GetMapping("/{date}")
    public ApiResponse<DiaryResponse.Detail> getDiaryByDate(
            @AuthenticationPrincipal User user,

            @Parameter(description = "조회할 날짜 (YYYY-MM-DD)", example = "2025-11-05")
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ApiResponse.ok(diaryService.getDiaryByDate(user.getUserId(), date));
    }

    // 4. 삭제
    @Operation(summary = "일기 삭제")
    @DeleteMapping("/{date}")
    public ApiResponse<Map<String, String>> deleteDiary(
            @AuthenticationPrincipal User user,

            @Parameter(description = "삭제할 날짜 (YYYY-MM-DD)", example = "2025-11-05")
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        diaryService.deleteDiary(user.getUserId(), date);

        Map<String, String> data = new HashMap<>();
        data.put("message", "일기가 삭제되었습니다.");

        return ApiResponse.ok(data);
    }
}
