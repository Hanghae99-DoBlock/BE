package com.sparta.doblock.todo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class TodoResponseDto {
    private Long todoId;
    private String todo;
    private List<String> tagList;
}
