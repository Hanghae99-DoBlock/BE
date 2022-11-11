package com.sparta.doblock.todo.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class TodoRequestDto {

    private int year;
    private int month;
    private int day;
    private String todoContent;
    private List<String> tagList;
    private boolean completed;
}
