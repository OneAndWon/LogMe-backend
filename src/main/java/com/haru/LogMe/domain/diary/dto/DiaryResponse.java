package com.haru.LogMe.domain.diary.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.haru.LogMe.domain.diary.entity.Diary;
import com.haru.LogMe.domain.diary.entity.DiaryAttachment;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class DiaryResponse {

    // === 1. 상세 조회 및 Upsert 결과용 (전체 데이터) ===
    @Getter
    public static class Detail {
        @JsonProperty("diary_id")
        private Long diaryId;

        @JsonProperty("user_id")
        private Long userId;

        private LocalDate date;

        @JsonProperty("content_text")
        private String content;

        @JsonProperty("emotion_icon")
        private String emotionIcon;

        private List<AttachmentDto> attachments;

        public Detail(Diary diary) {
            this.diaryId = diary.getDiaryId();
            this.userId = diary.getUser().getUserId(); // [수정] User 객체에서 ID 추출
            this.date = diary.getDate();
            this.content = diary.getContent();
            this.emotionIcon = diary.getEmotionIcon();
            this.attachments = diary.getAttachments().stream()
                    .map(AttachmentDto::new)
                    .collect(Collectors.toList());
        }
    }

    @Getter
    public static class Summary {
        @JsonProperty("diary_id")
        private Long diaryId;

        private LocalDate date;

        @JsonProperty("emotion_icon")
        private String emotionIcon;

        public Summary(Diary diary) {
            this.diaryId = diary.getDiaryId();
            this.date = diary.getDate();
            this.emotionIcon = diary.getEmotionIcon();
        }
    }

    @Getter
    public static class AttachmentDto {
        @JsonProperty("attachment_id")
        private Long attachmentId;

        @JsonProperty("file_url")
        private String fileUrl;

        @JsonProperty("file_type")
        private String fileType;

        public AttachmentDto(DiaryAttachment attachment) {
            this.attachmentId = attachment.getAttachmentId();
            this.fileUrl = attachment.getFileUrl();
            this.fileType = attachment.getFileType();
        }
    }
}
