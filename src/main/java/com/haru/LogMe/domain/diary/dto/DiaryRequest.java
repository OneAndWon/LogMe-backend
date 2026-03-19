package com.haru.LogMe.domain.diary.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class DiaryRequest {
    @NotNull(message = "날짜는 필수입니다.")
    private LocalDate date;

    private String title;

    @JsonProperty("content_text")
    private String content;

    @JsonProperty("emotion_icon")
    private String emotionIcon;

}
