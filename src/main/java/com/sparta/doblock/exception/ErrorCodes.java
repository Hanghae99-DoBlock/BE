package com.sparta.doblock.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCodes {

    NOT_FOUND_MEMBER("NOT_FOUND_MEMBER", "가입되지 않은 회원입니다."),
    NOT_FOUND_COURSE("NOT_FOUND_COURSE", "등록되지 않은 강의입니다."),
    NOT_FOUND_REVIEW("NOT_FOUND_REVIEW", "등록되지 않은 리뷰입니다."),
    NOT_FOUND_PAYMENT("NOT_FOUND_PAYMENT", "찾을 수 없는 결제 내역입니다."),
    NOT_MATCHED_PASSWORD("NOT_MATCHED_PASSWORD", "비밀번호가 일치하지 않습니다."),
    DUPLICATED_EMAIL("DUPLICATED_EMAIL", "이미 가입된 이메일입니다."),
    NOT_VALID_FORMAT("NOT_VALID_FORMAT", "지정된 양식을 확인해주세요."),
    NOT_VALID_WRITER("NOT_VALID_WRITER", "작성자가 아닙니다."),
    NOT_VALID_ADMIN("NOT_VALID_ADMIN", "관리자가 아닙니다."),
    NOT_VALID_TOKEN("NOT_VALID_TOKEN", "서명이 유효하지 않습니다."),
    UPLOAD_FAIL("UPLOAD_FAIL", "파일 업로드에 실패했습니다."),
    NOT_AUTHENTICATION_ENTRY("AUTHENTICATION_ENTRY_ERROR", "접근이 잘못되었습니다."),
    ACCESS_DENIED("AUTHENTICATION_ENTRY_ERROR", "접근이 잘못되었습니다.");

    private final String code;
    private final String message;
}
