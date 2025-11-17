package com.haru.LogMe.domain.diary.entity;

import com.haru.LogMe.domain.common.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(
        name = "diary",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"user_id", "date"})
        }
)
public class Diary extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "diary_id")
    private Long diaryId;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "content_text", columnDefinition = "TEXT")
    private String content; // ERD: content_text

    @Column(name = "emotion_icon")
    private String emotionIcon; // ERD: emotion_icon

    // 1:N 관계 (일기 삭제 시 첨부파일도 삭제, 고아 객체 제거 활성화)
    @OneToMany(mappedBy = "diary", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DiaryAttachment> attachments = new ArrayList<>();

    @Builder
    public Diary(Long userId, LocalDate date, String content, String emotionIcon) {
        this.userId = userId;
        this.date = date;
        this.content = content;
        this.emotionIcon = emotionIcon;
    }

    // --- 비즈니스 로직 ---

    // 내용 수정 및 첨부파일 교체 (Upsert용)
    public void update(String content, String emotionIcon, List<DiaryAttachment> newAttachments) {
        this.content = content;
        this.emotionIcon = emotionIcon;

        // 기존 첨부파일 리스트를 비우고 새로운 리스트로 교체 (orphanRemoval 동작)
        this.attachments.clear();
        if (newAttachments != null) {
            for (DiaryAttachment attachment : newAttachments) {
                this.addAttachment(attachment); // 편의 메서드 호출
            }
        }
    }

    // 연관관계 편의 메서드
    public void addAttachment(DiaryAttachment attachment) {
        this.attachments.add(attachment);
        attachment.setDiary(this);
    }
}
