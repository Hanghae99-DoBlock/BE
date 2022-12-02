package com.sparta.doblock.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(DoBlockExceptions.class)
    public ResponseEntity<?> handleDoBlockException(DoBlockExceptions doblockExceptions){
        return ResponseEntity.status(doblockExceptions.getErrorCodes().getHttpStatus()).body(new ErrorMessage(doblockExceptions.getErrorCodes().getCode(), doblockExceptions.getErrorCodes().getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleNotValidFormatException(){
        return ResponseEntity.status(ErrorCodes.NOT_VALID_FORMAT.getHttpStatus()).body(new ErrorMessage(ErrorCodes.NOT_VALID_FORMAT.getCode(), ErrorCodes.NOT_VALID_FORMAT.getMessage()));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<?> handleMaxUploadSizeExceededException() {
        return ResponseEntity.status(ErrorCodes.EXCEED_FILE_SIZE.getHttpStatus()).body(new ErrorMessage(ErrorCodes.EXCEED_FILE_SIZE.getCode(), ErrorCodes.EXCEED_FILE_SIZE.getMessage()));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?> handleAuthenticationException(Exception exception){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(exception.getMessage());
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleAccessDeniedException(Exception exception){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(exception.getMessage());
    }
}
