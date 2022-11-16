package com.sparta.doblock.todo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class TodoOrderRequestDto {

    private int year;
    private int month;
    private int day;
    private List<TodoRequestDto> todoRequestDtoList;
}
