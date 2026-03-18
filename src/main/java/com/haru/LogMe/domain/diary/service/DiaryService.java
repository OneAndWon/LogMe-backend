package com.haru.LogMe.domain.diary.service;

import com.haru.LogMe.domain.diary.Repository.DiaryRepository;
import com.haru.LogMe.domain.diary.dto.DiaryRequest;
import com.haru.LogMe.domain.diary.dto.DiaryResponse;
import com.haru.LogMe.domain.diary.entity.Diary;
import com.haru.LogMe.domain.diary.entity.DiaryAttachment;
import com.haru.LogMe.domain.user.entity.User;
import com.haru.LogMe.global.exception.CustomException;
import com.haru.LogMe.global.exception.ErrorCode;
import com.haru.LogMe.global.response.ListResponse;
import com.haru.LogMe.global.util.FileStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DiaryService {

    private final DiaryRepository diaryRepository;
    //private final FileStore fileStore;

    // 1. 일기 생성 및 수정 (Upsert)
    @Transactional
    public DiaryResponse.Detail upsertDiary(User user, DiaryRequest dto) {

        Diary diary = diaryRepository.findByUserAndDate(user, dto.getDate())
                .map(existingDiary -> {
                    existingDiary.update(dto.getTitle(), dto.getContent(), dto.getEmotionIcon());
                    return existingDiary;
                })
                .orElseGet(() -> {
                    Diary newDiary = Diary.builder()
                            .user(user)
                            .date(dto.getDate())
                            .title(dto.getTitle())
                            .content(dto.getContent())
                            .emotionIcon(dto.getEmotionIcon())
                            .build();
                    return diaryRepository.save(newDiary);
                });

        return new DiaryResponse.Detail(diary);
    }

    // 2. 일기 목록 조회 (캘린더 뷰) - 반환 타입 변경 (Detail -> Summary)
    @Transactional(readOnly = true)
    public ListResponse<DiaryResponse.Summary> getDiaries(User user, String monthParam) {
        YearMonth yearMonth;
        try {
            yearMonth = YearMonth.parse(monthParam);
        } catch (Exception e) {
            throw new CustomException(ErrorCode.INVALID_INPUT);
        }

        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        List<DiaryResponse.Summary> list = diaryRepository.findAllByUserAndDateBetweenOrderByDateAsc(user, startDate, endDate).stream()
                .map(DiaryResponse.Summary::new)
                .collect(Collectors.toList());

        return ListResponse.of(list);
    }

    // 3. 상세 조회
    @Transactional(readOnly = true)
    public DiaryResponse.Detail getDiaryByDate(User user, LocalDate date) {
        Diary diary = diaryRepository.findByUserAndDate(user, date)
                .orElseThrow(() -> new CustomException(ErrorCode.DIARY_NOT_FOUND));
        return new DiaryResponse.Detail(diary);
    }

    // 4. 삭제
    @Transactional
    public void deleteDiary(User user, LocalDate date) {
        Diary diary = diaryRepository.findByUserAndDate(user, date)
                .orElseThrow(() -> new CustomException(ErrorCode.DIARY_NOT_FOUND));
        diaryRepository.delete(diary);
    }

    // 5. 일기 검색 (3가지 검색 방식: 제목, 내용, 통합)
    @Transactional(readOnly = true)
    public ListResponse<DiaryResponse.Summary> searchDiaries(User user, String keyword, String searchType) {
        List<Diary> diaries;

        if ("TITLE".equalsIgnoreCase(searchType)) {
            diaries = diaryRepository.findAllByUserAndTitleContainingIgnoreCaseOrderByDateDesc(user, keyword);
        } else if ("CONTENT".equalsIgnoreCase(searchType)) {
            diaries = diaryRepository.findAllByUserAndContentContainingIgnoreCaseOrderByDateDesc(user, keyword);
        } else {
            // "ALL" 이거나 잘못된 값이 들어왔을 때 기본으로 제목+내용 통합 검색
            diaries = diaryRepository.findByUserAndKeywordInTitleOrContent(user, keyword);
        }

        List<DiaryResponse.Summary> list = diaries.stream()
                .map(DiaryResponse.Summary::new)
                .collect(Collectors.toList());

        return ListResponse.of(list);
    }
/*
    // 5. 첨부파일 업로드
    @Transactional
    public String uploadDiaryImage(User user, LocalDate date, MultipartFile file) {
        // 1. 일기 조회
        Diary diary = diaryRepository.findByUserAndDate(user, date)
                .orElseThrow(() -> new CustomException(ErrorCode.DIARY_NOT_FOUND));

        // 2. 기존 이미지가 있다면 삭제 (1:1 유지를 위해)
        // orphanRemoval = true 설정 덕분에 list.clear()시 DB에서도 삭제됨
        if (!diary.getAttachments().isEmpty()) {
            // 물리 파일 먼저 삭제
            for (DiaryAttachment old : diary.getAttachments()) {
                fileStore.deleteFile(old.getFileUrl());
            }
            // DB 연관관계 끊기 (삭제)
            diary.getAttachments().clear();
        }

        // 3. 새 파일 저장
        String storedFilePath;
        try {
            storedFilePath = fileStore.storeFile(file);
        } catch (IOException e) {
            // 파일 저장 실패 시 예외 발생 (CustomException에 FILE_UPLOAD_ERROR 추가 필요)
            throw new CustomException(ErrorCode.FILE_UPLOAD_ERROR);
        }

        // 4. 엔티티 생성 및 연결
        if (storedFilePath != null) {
            DiaryAttachment newAttachment = DiaryAttachment.builder()
                    .fileUrl(storedFilePath)
                    .fileType("IMAGE") // 필요 시 file.getContentType() 사용
                    .build();

            diary.addAttachment(newAttachment);
        }

        return storedFilePath;
    }

    //6. 첨부파일 삭제
    @Transactional
    public void deleteDiaryImage(User user, LocalDate date) {
        Diary diary = diaryRepository.findByUserAndDate(user, date)
                .orElseThrow(() -> new CustomException(ErrorCode.DIARY_NOT_FOUND));

        if (!diary.getAttachments().isEmpty()) {
            // 물리 파일 삭제
            diary.getAttachments().forEach(a -> fileStore.deleteFile(a.getFileUrl()));
            // DB 데이터 삭제
            diary.getAttachments().clear();
        }
    }*/
}
