package com.haru.LogMe.domain.diary.controller;

import com.haru.LogMe.domain.diary.dto.DiaryRequest;
import com.haru.LogMe.domain.diary.dto.DiaryResponse;
import com.haru.LogMe.domain.diary.service.DiaryService;
import com.haru.LogMe.domain.user.entity.User;
import com.haru.LogMe.global.response.ApiResponse;
import com.haru.LogMe.global.response.ListResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
        return ApiResponse.ok(diaryService.upsertDiary(user, dto));
    }

    // 2. 목록 조회 (수정됨)
    @Operation(summary = "일기 목록 조회 (캘린더용)")
    @GetMapping
    public ApiResponse<ListResponse<DiaryResponse.Summary>> getDiaries(
            @AuthenticationPrincipal User user,
            @RequestParam(name = "year-month") String yearMonth
    ) {
        return ApiResponse.ok(diaryService.getDiaries(user, yearMonth));
    }

    // 3. 상세 조회
    @Operation(summary = "일기 상세 조회", description = "특정 날짜의 일기 상세 정보를 조회합니다.")
    @GetMapping("/{date}")
    public ApiResponse<DiaryResponse.Detail> getDiaryByDate(
            @AuthenticationPrincipal User user,

            @Parameter(description = "조회할 날짜 (YYYY-MM-DD)", example = "2025-11-05")
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        return ApiResponse.ok(diaryService.getDiaryByDate(user, date));
    }

    // 4. 삭제
    @Operation(summary = "일기 삭제")
    @DeleteMapping("/{date}")
    public ApiResponse<Map<String, String>> deleteDiary(
            @AuthenticationPrincipal User user,

            @Parameter(description = "삭제할 날짜 (YYYY-MM-DD)", example = "2025-11-05")
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        diaryService.deleteDiary(user, date);

        Map<String, String> data = new HashMap<>();
        data.put("message", "일기가 삭제되었습니다.");

        return ApiResponse.ok(data);
    }

    // 5. 첨부파일 업로드
    @Operation(summary = "일기 이미지 업로드", description = "특정 날짜의 일기에 이미지를 업로드합니다. (기존 이미지는 삭제되고 교체됨)")
    @PostMapping(value = "/{date}/images", consumes = "multipart/form-data")
    public ApiResponse<Map<String, String>> uploadImage(
            @AuthenticationPrincipal User user,
            @Parameter(description = "날짜 (YYYY-MM-DD)", example = "2025-11-05")
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam("file") MultipartFile file
    ) {
        String newUrl = diaryService.uploadDiaryImage(user, date, file);

        // 프론트엔드에 URL을 줘서 바로 <img> src를 바꿀 수 있게 함
        Map<String, String> responseData = new HashMap<>();
        responseData.put("newFileUrl", newUrl);

        return ApiResponse.ok(responseData);
    }

    //6. 첨부파일 삭제
    @Operation(summary = "일기 이미지 삭제", description = "특정 날짜 일기의 이미지를 삭제합니다.")
    @DeleteMapping("/{date}/images")
    public ApiResponse<String> deleteImage(
            @AuthenticationPrincipal User user,
            @Parameter(description = "날짜 (YYYY-MM-DD)", example = "2025-11-05")
            @PathVariable @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        diaryService.deleteDiaryImage(user, date);
        return ApiResponse.ok("이미지가 삭제되었습니다.");
    }
}
