package com.haru.LogMe.domain.diary.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Emotion {
    HAPPY("기쁨 / 뿌듯함"),
    CALM("평온 / 무난함"),
    SAD("우울 / 슬픔"),
    ANGRY("분노 / 스트레스"),
    TIRED("피곤 / 지침");

    private final String description;
}
