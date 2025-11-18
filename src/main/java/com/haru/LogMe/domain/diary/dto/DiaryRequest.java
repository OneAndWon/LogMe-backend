package com.haru.LogMe.domain.diary.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.haru.LogMe.domain.diary.entity.Diary;
import com.haru.LogMe.domain.diary.entity.DiaryAttachment;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Getter
@NoArgsConstructor
public class DiaryRequest {
    @NotNull(message = "날짜는 필수입니다.")
    private LocalDate date;

    @JsonProperty("content_text")
    private String content;

    @JsonProperty("emotion_icon")
    private String emotionIcon;

    private List<AttachmentRequest> attachments;

    @Getter
    @NoArgsConstructor
    public static class AttachmentRequest {
        @JsonProperty("file_url")
        private String fileUrl;

        @JsonProperty("file_type")
        private String fileType;
    }
}
