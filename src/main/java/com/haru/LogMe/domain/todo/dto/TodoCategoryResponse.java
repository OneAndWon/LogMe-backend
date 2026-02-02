package com.haru.LogMe.domain.todo.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.haru.LogMe.domain.todo.entity.TodoCategory;
import lombok.Getter;

@Getter
public class TodoCategoryResponse {
    @JsonProperty("todo_category_id")
    private Long todoCategoryId;
    private String name;
    private String color;

    public TodoCategoryResponse(TodoCategory todoCategory) {
        this.todoCategoryId = todoCategory.getTodoCategoryId();
        this.name = todoCategory.getName();
        this.color = todoCategory.getColor();
    }
}
