package com.sparta.doblock.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ErrorMessage {

    private String code;
    private String message;
}
