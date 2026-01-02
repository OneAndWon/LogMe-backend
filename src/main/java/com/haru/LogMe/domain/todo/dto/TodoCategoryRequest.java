package com.haru.LogMe.domain.todo.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class TodoCategoryRequest {
    @NotBlank(message = "카테고리 이름은 필수입니다.")
    private String name;

    @NotBlank(message = "색상 코드는 필수입니다.")
    private String color;

}
