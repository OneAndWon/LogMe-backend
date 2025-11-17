package com.haru.LogMe.domain.diary.service;

import com.haru.LogMe.domain.diary.Repository.DiaryRepository;
import com.haru.LogMe.domain.diary.dto.DiaryRequest;
import com.haru.LogMe.domain.diary.dto.DiaryResponse;
import com.haru.LogMe.domain.diary.entity.Diary;
import com.haru.LogMe.domain.diary.entity.DiaryAttachment;
import com.haru.LogMe.global.exception.CustomException;
import com.haru.LogMe.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;

    // 1. 일기 생성 및 수정 (Upsert)
    @Transactional
    public DiaryResponse.Detail upsertDiary(Long userId, DiaryRequest dto) {
        // (기존 로직 유지)
        List<DiaryAttachment> newAttachments = (dto.getAttachments() == null) ?
                List.of() :
                dto.getAttachments().stream()
                        .map(a -> DiaryAttachment.builder()
                                .fileUrl(a.getFileUrl())
                                .fileType(a.getFileType())
                                .build())
                        .collect(Collectors.toList());

        Diary diary = diaryRepository.findByUserIdAndDate(userId, dto.getDate())
                .map(existingDiary -> {
                    existingDiary.update(dto.getContent(), dto.getEmotionIcon(), newAttachments);
                    return existingDiary;
                })
                .orElseGet(() -> {
                    Diary newDiary = Diary.builder()
                            .userId(userId)
                            .date(dto.getDate())
                            .content(dto.getContent())
                            .emotionIcon(dto.getEmotionIcon())
                            .build();
                    for (DiaryAttachment attachment : newAttachments) {
                        newDiary.addAttachment(attachment);
                    }
                    return diaryRepository.save(newDiary);
                });

        return new DiaryResponse.Detail(diary);
    }

    // 2. 일기 목록 조회 (캘린더 뷰) - 반환 타입 변경 (Detail -> Summary)
    public List<DiaryResponse.Summary> getDiaries(Long userId, String monthParam) {
        // "2025-11" 같은 문자열을 파싱
         YearMonth yearMonth;
        try {
            yearMonth = YearMonth.parse(monthParam); // YYYY-MM 형식 파싱
        } catch (Exception e) {
            // 형식이 틀리면 400 Bad Request
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }

        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        return diaryRepository.findAllByUserIdAndDateBetweenOrderByDateAsc(userId, startDate, endDate).stream()
                .map(DiaryResponse.Summary::new) // Summary DTO로 변환
                .collect(Collectors.toList());
    }

    // 3. 상세 조회
    public DiaryResponse.Detail getDiaryByDate(Long userId, LocalDate date) {
        Diary diary = diaryRepository.findByUserIdAndDate(userId, date)
                .orElseThrow(() -> new CustomException(ErrorCode.DIARY_NOT_FOUND));
        return new DiaryResponse.Detail(diary);
    }

    // 4. 삭제
    @Transactional
    public void deleteDiary(Long userId, LocalDate date) {
        Diary diary = diaryRepository.findByUserIdAndDate(userId, date)
                .orElseThrow(() -> new CustomException(ErrorCode.DIARY_NOT_FOUND));
        diaryRepository.delete(diary);
    }
}
