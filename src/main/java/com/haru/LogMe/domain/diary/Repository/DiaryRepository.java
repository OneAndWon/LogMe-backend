package com.haru.LogMe.domain.diary.Repository;

import com.haru.LogMe.domain.diary.entity.Diary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
    // 특정 유저의 특정 날짜 일기 조회
    Optional<Diary> findByUserIdAndDate(Long userId, LocalDate date);

    // 캘린더 뷰용: 특정 기간(월) 내의 일기 목록 조회
    // 날짜 순으로 정렬하여 가져옴
    List<Diary> findAllByUserIdAndDateBetweenOrderByDateAsc(Long userId, LocalDate startDate, LocalDate endDate);
}
