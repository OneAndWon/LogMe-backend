package com.haru.LogMe.domain.dashboard.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Getter
@NoArgsConstructor
public class MottoRequest {
    @NotNull(message = "날짜는 필수입니다.")
    private LocalDate date;

    @NotBlank(message = "다짐을 입력해주세요.")
    private String motto;
}
