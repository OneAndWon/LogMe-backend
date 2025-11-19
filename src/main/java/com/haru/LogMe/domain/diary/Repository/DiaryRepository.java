package com.haru.LogMe.domain.diary.Repository;

import com.haru.LogMe.domain.diary.entity.Diary;
import com.haru.LogMe.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DiaryRepository extends JpaRepository<Diary, Long> {
    // User 객체로 조회
    Optional<Diary> findByUserAndDate(User user, LocalDate date);

    // User 객체로 기간 조회
    List<Diary> findAllByUserAndDateBetweenOrderByDateAsc(User user, LocalDate startDate, LocalDate endDate);
}
