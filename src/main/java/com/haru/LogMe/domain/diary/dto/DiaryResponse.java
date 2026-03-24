package com.haru.LogMe.domain.diary.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.haru.LogMe.domain.diary.entity.Diary;
import com.haru.LogMe.domain.diary.entity.DiaryAttachment;
import com.haru.LogMe.domain.diary.entity.Emotion;
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

        private String title;

        @JsonProperty("content_text")
        private String content;

        @JsonProperty("emotion_icon")
        private Emotion emotionIcon;

        //private AttachmentDto attachment;

        public Detail(Diary diary) {
            this.diaryId = diary.getDiaryId();
            this.userId = diary.getUser().getUserId();
            this.date = diary.getDate();
            this.title = diary.getTitle();
            this.content = diary.getContent();
            this.emotionIcon = diary.getEmotionIcon();

            // 리스트에서 첫 번째 사진만 꺼내거나 없으면 null 처리
            /*if (!diary.getAttachments().isEmpty()) {
                this.attachment = new AttachmentDto(diary.getAttachments().get(0));
            } else {
                this.attachment = null;
            }*/
        }
    }

    @Getter
    public static class Summary {
        @JsonProperty("diary_id")
        private Long diaryId;

        private LocalDate date;

        private String title;

        @JsonProperty("emotion_icon")
        private Emotion emotionIcon;

        public Summary(Diary diary) {
            this.diaryId = diary.getDiaryId();
            this.date = diary.getDate();
            this.title = diary.getTitle();
            this.emotionIcon = diary.getEmotionIcon();
        }
    }

    /*@Getter
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
    }*/
}
