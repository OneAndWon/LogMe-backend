package com.haru.LogMe.domain.diary.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
@Table(name = "diary_attachment")*/
public class DiaryAttachment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "attachment_id")
    private Long attachmentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_id")
    private Diary diary;

    @Column(name = "file_url", nullable = false)
    private String fileUrl;

    @Column(name = "file_type", nullable = false)
    private String fileType; // "photo", "video"

    @Builder
    public DiaryAttachment(String fileUrl, String fileType) {
        this.fileUrl = fileUrl;
        this.fileType = fileType;
    }

    // 연관관계 편의 메서드용 Setter (Diary 쪽에서 호출)
    public void setDiary(Diary diary) {
        this.diary = diary;
    }
}
