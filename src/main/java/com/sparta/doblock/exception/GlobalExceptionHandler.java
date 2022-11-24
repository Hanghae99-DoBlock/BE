package com.sparta.doblock.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // TODO: OAuth2AuthenticationException
    @ExceptionHandler(NullPointerException.class)
    public ResponseEntity<?> handleNullPointerException(NullPointerException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(IllegalAccessException.class)
    public ResponseEntity<?> handleIllegalAccessException(IllegalAccessException e) {
        return new ResponseEntity<>(e.getMessage(), HttpStatus.FORBIDDEN);
    }

    // General Exception
    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception e) {
        return new ResponseEntity<>(e.toString(), HttpStatus.BAD_REQUEST);
    }

    /*
    ------- CUSTOM EXCEPTIONS --------
     */

    @ExceptionHandler(CustomExceptions.NotFoundMemberException.class)
    public ResponseEntity<?> handleNotFoundMemberException(){
        return new ResponseEntity<>(new ErrorMessage(ErrorCodes.NOT_FOUND_MEMBER.getCode(), ErrorCodes.NOT_FOUND_MEMBER.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CustomExceptions.NotFoundCourseException.class)
    public ResponseEntity<?> handleNotFoundCourseException(){
        return new ResponseEntity<>(new ErrorMessage(ErrorCodes.NOT_FOUND_COURSE.getCode(), ErrorCodes.NOT_FOUND_COURSE.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CustomExceptions.NotFoundReviewException.class)
    public ResponseEntity<?> handleNotFoundReviewException(){
        return new ResponseEntity<>(new ErrorMessage(ErrorCodes.NOT_FOUND_REVIEW.getCode(), ErrorCodes.NOT_FOUND_REVIEW.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CustomExceptions.NotFoundPaymentException.class)
    public ResponseEntity<?> handleNotFoundPaymentException(){
        return new ResponseEntity<>(new ErrorMessage(ErrorCodes.NOT_FOUND_PAYMENT.getCode(), ErrorCodes.NOT_FOUND_PAYMENT.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(CustomExceptions.NotMatchedPasswordException.class)
    public ResponseEntity<?> handleNotMatchedPasswordException(){
        return new ResponseEntity<>(new ErrorMessage(ErrorCodes.NOT_MATCHED_PASSWORD.getCode(), ErrorCodes.NOT_MATCHED_PASSWORD.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CustomExceptions.DuplicatedEmailException.class)
    public ResponseEntity<?> handleDuplicatedEmailException(){
        return new ResponseEntity<>(new ErrorMessage(ErrorCodes.DUPLICATED_EMAIL.getCode(), ErrorCodes.DUPLICATED_EMAIL.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleNotValidFormatException(){
        return new ResponseEntity<>(new ErrorMessage(ErrorCodes.NOT_VALID_FORMAT.getCode(), ErrorCodes.NOT_VALID_FORMAT.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CustomExceptions.NotValidWriterException.class)
    public ResponseEntity<?> handleNotValidWriterException(){
        return new ResponseEntity<>(new ErrorMessage(ErrorCodes.NOT_VALID_WRITER.getCode(), ErrorCodes.NOT_VALID_WRITER.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(CustomExceptions.NotValidAdminException.class)
    public ResponseEntity<?> handleNotValidAdminException(){
        return new ResponseEntity<>(new ErrorMessage(ErrorCodes.NOT_VALID_ADMIN.getCode(), ErrorCodes.NOT_VALID_ADMIN.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(CustomExceptions.UploadFailException.class)
    public ResponseEntity<?> handleUploadFailException(){
        return new ResponseEntity<>(new ErrorMessage(ErrorCodes.UPLOAD_FAIL.getCode(), ErrorCodes.UPLOAD_FAIL.getMessage()), HttpStatus.UNSUPPORTED_MEDIA_TYPE);
    }

}