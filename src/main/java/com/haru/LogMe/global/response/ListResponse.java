package com.haru.LogMe.global.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ListResponse<T> {
    private List<T> content;
    private long totalElements;

    // 리스트를 넣으면 자동으로 totalElements 계산
    public static <T> ListResponse<T> of(List<T> list) {
        return ListResponse.<T>builder()
                .content(list)
                .totalElements(list.size())
                .build();
    }
}
