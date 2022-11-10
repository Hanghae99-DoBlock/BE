package com.sparta.doblock.todo.dto.response;

import com.sparta.doblock.todo.entity.Todo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class TodoResponseDto {
    private Long todoId;

    private String todoContent;
    private List<String> tagList;
    private boolean completed;

}
