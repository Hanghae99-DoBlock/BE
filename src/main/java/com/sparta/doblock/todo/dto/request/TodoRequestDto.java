package com.sparta.doblock.todo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TodoRequestDto {

    private int year;
    private int month;
    private int day;
    private Long todoId;
    private String todoContent;
    private boolean completed;
    private String todoMemo;
}
