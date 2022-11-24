package com.sparta.doblock.todo.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class TodoResponseDto {

    private Long todoId;
    private String todoContent;
    private boolean completed;
    private String todoMemo;
    private int day;
}
