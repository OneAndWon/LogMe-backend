package com.haru.LogMe.domain.dashboard.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.haru.LogMe.domain.dashboard.entity.DailySummary;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class MottoResponse {
    @JsonProperty("daily_summary_id")
    private Long dailySummaryId;

    private LocalDate date;

    private String motto;

    public MottoResponse(DailySummary dailySummary) {
        this.dailySummaryId = dailySummary.getId();
        this.date = dailySummary.getDate();
        this.motto = dailySummary.getMotto();
    }
}
