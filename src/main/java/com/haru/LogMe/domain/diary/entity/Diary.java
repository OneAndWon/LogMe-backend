package com.haru.LogMe.domain.diary.entity;

import com.haru.LogMe.domain.common.BaseTimeEntity;
import com.haru.LogMe.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDate;
import java.time.LocalDateTime;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDate date;

    @Column(name = "title")
    private String title;

    @Column(name = "content_text", columnDefinition = "TEXT")
    private String content;

    @Column(name = "emotion_icon")
    @Enumerated(EnumType.STRING) // DB에 문자열(ex: "HAPPY")로 저장
    private Emotion emotionIcon;

    // 1:N 관계 (일기 삭제 시 첨부파일도 삭제, 고아 객체 제거 활성화)
    /*@OneToMany(mappedBy = "diary", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DiaryAttachment> attachments = new ArrayList<>();
    */

    @Builder
    public Diary(User user, LocalDate date, String title, String content, Emotion emotionIcon) {
        this.user = user;
        this.date = date;
        this.title = title;
        this.content = content;
        this.emotionIcon = emotionIcon;
    }

    // --- 비즈니스 로직 ---

    // 내용 수정 (Upsert용)
    public void update(String title, String content, Emotion emotionIcon) {
        this.title = title;
        this.content = content;
        this.emotionIcon = emotionIcon;
    }

    // 연관관계 편의 메서드
    /*public void addAttachment(DiaryAttachment attachment) {
        this.attachments.add(attachment);
        attachment.setDiary(this);
    }*/
}
