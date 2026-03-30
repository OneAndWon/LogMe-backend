package com.haru.LogMe.domain.diary.Repository;

import com.haru.LogMe.domain.diary.entity.Diary;
import com.haru.LogMe.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
    // User 객체로 조회
    Optional<Diary> findByUserAndDate(User user, LocalDate date);

    // User 객체로 기간 조회
    List<Diary> findAllByUserAndDateBetweenOrderByDateAsc(User user, LocalDate startDate, LocalDate endDate);

    //======= 검색 쿼리

    // 1. 제목 검색
    List<Diary> findAllByUserAndTitleContainingIgnoreCaseOrderByDateDesc(User user, String keyword);

    // 2. 내용 검색
    List<Diary> findAllByUserAndContentContainingIgnoreCaseOrderByDateDesc(User user, String keyword);

    // 3. 제목 + 내용 혼합 검색 (@Query 사용으로 안전하게 묶음)
    @Query("SELECT d FROM Diary d WHERE d.user = :user AND (LOWER(d.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR LOWER(d.content) LIKE LOWER(CONCAT('%', :keyword, '%'))) ORDER BY d.date DESC")
    List<Diary> findByUserAndKeywordInTitleOrContent(@Param("user") User user, @Param("keyword") String keyword);

    // 리포트용
    List<Diary> findAllByUserAndDateBetween(User user, LocalDate start, LocalDate end);

}
