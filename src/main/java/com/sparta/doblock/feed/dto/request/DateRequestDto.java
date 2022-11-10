package com.sparta.doblock.feed.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DateRequestDto {

    private int year;
    private int month;
    private int day;
}
