package com.sparta.doblock.exception;

public class CustomExceptions {

    public static class NotFoundMemberException extends RuntimeException{
    }
    public static class NotFoundCourseException extends RuntimeException{
    }
    public static class NotFoundReviewException extends RuntimeException{
    }
    public static class NotFoundPaymentException extends RuntimeException{
    }
    public static class NotMatchedPasswordException extends RuntimeException{
    }
    public static class DuplicatedEmailException extends RuntimeException{
    }
    public static class NotValidWriterException extends RuntimeException{
    }
    public static class NotValidAdminException extends RuntimeException{
    }
    public static class UploadFailException extends RuntimeException{
    }
}
