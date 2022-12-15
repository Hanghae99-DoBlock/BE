package com.sparta.doblock.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DoBlockExceptions extends RuntimeException{

    private ErrorCodes errorCodes;
}
