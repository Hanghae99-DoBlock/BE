package com.sparta.doblock.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCodes {

    DUPLICATED_EMAIL(HttpStatus.BAD_REQUEST, "DUPLICATED_EMAIL", "이미 사용 중인 이메일입니다."),
    DUPLICATED_NICKNAME(HttpStatus.BAD_REQUEST, "DUPLICATED_NICKNAME", "이미 사용 중인 닉네임입니다."),
    NOT_VALID_FORMAT(HttpStatus.BAD_REQUEST, "NOT_VALID_FORMAT", "지정된 양식을 사용해주세요."),
    NOT_VALID_PASSWORD(HttpStatus.BAD_REQUEST, "NOT_VALID_PASSWORD", "비밀번호를 다시 확인해주세요."),
    NOT_INPUT_INFORMATION(HttpStatus.BAD_REQUEST, "NOT_INPUT_INFORMATION", "변경될 정보를 입력해주세요."),
    NOT_INPUT_BADGES(HttpStatus.BAD_REQUEST, "NOT_INPUT_BADGES", "뱃지를 선택해주세요."),
    NOT_COMPLETED_TODO(HttpStatus.BAD_REQUEST, "NOT_COMPLETED_TODO", "완료된 투두만 등록 가능합니다."),
    NOT_MATCHED_TODO_COUNT(HttpStatus.BAD_REQUEST, "NOT_MATCHED_TODO_COUNT", "투두 리스트 갯수를 다시 확인해주세요."),
    NOT_MATCHED_TODO_DATE(HttpStatus.BAD_REQUEST, "NOT_MATCHED_TODO_DATE", "투두의 날짜를 다시 확인해주세요."),
    NOT_MATCHED_FEED_COMMENT(HttpStatus.BAD_REQUEST, "NOT_MATCHED_TODO_COMMENT", "피드와 일치하지 않는 댓글입니다."),
    NOT_ABLE_COMPLETE_TODO(HttpStatus.BAD_REQUEST, "NOT_ABLE_COMPLETE_TODO", "미래의 투두는 완료할 수 없습니다."),
    NOT_ABLE_SEND_MESSAGE(HttpStatus.BAD_REQUEST, "NOT_ABLE_SEND_MESSAGE", "본인에게 메세지를 보낼 수 없습니다."),
    NOT_ABLE_FOLLOW(HttpStatus.BAD_REQUEST, "NOT_ABLE_FOLLOW", "본인을 팔로우할 수 없습니다."),
    EXCEED_FEED_CONTENT(HttpStatus.BAD_REQUEST, "EXCEED_FEED_CONTENT", "피드 내용은 최대 100자까지 등록 가능합니다."),
    EXCEED_FEED_IMAGE(HttpStatus.BAD_REQUEST, "EXCEED_FEED_IMAGE", "피드 사진은 최대 4개까지 등록 가능합니다."),
    EXCEED_FILE_SIZE(HttpStatus.BAD_REQUEST, "EXCEED_FILE_SIZE", "이미지는 1장당 최대 5MB까지 등록 가능합니다."),
    EXCEED_MEMBER_TAG(HttpStatus.BAD_REQUEST, "EXCEED_MEMBER_TAG", "관심사 태그는 최대 3개까지 등록 가능합니다."),
    NOT_OBTAINED_BADGES(HttpStatus.BAD_REQUEST, "NOT_OBTAINED_BADGES", "획득된 뱃지만 조회 가능합니다."),
    UPLOAD_IMAGE_FAILED(HttpStatus.BAD_REQUEST, "UPLOAD_IMAGE_FAILED", "파일 업로드에 실패했습니다."),

    NOT_VALID_AUTHENTICATION(HttpStatus.UNAUTHORIZED, "NOT_VALID_AUTHENTICATION", "인증이 유효하지 않습니다."),

    NOT_VALID_WRITER(HttpStatus.FORBIDDEN, "NOT_VALID_WRITER", "작성자만 이용 가능합니다."),
    NOT_VALID_CHATROOM_MEMBER(HttpStatus.FORBIDDEN, "NOT_VALID_CHATROOM_MEMBER", "채팅방을 이용할 수 없는 사용자입니다."),

    NOT_FOUND_MEMBER(HttpStatus.NOT_FOUND, "NOT_FOUND_MEMBER", "찾을 수 없는 사용자입니다."),
    NOT_FOUND_CHATROOM(HttpStatus.NOT_FOUND, "NOT_FOUND_CHATROOM", "찾을 수 없는 채팅방입니다."),
    NOT_FOUND_TODO(HttpStatus.NOT_FOUND, "NOT_FOUND_TODO", "찾을 수 없는 투두입니다."),
    NOT_FOUND_FEED(HttpStatus.NOT_FOUND, "NOT_FOUND_FEED", "찾을 수 없는 피드입니다."),
    NOT_FOUND_COMMENT(HttpStatus.NOT_FOUND, "NOT_FOUND_COMMENT", "찾을 수 없는 댓글입니다."),
    NOT_FOUND_TODO_DATE(HttpStatus.NOT_FOUND, "NOT_FOUND_TODO_DATE", "해당 날짜의 투두를 찾을 수 없습니다."),
    NOT_FOUND_PAYMENT(HttpStatus.NOT_FOUND, "NOT_FOUND_PAYMENT", "결제 내역을 찾을 수 없습니다."),
    NOT_FOUND_PAGE(HttpStatus.NOT_FOUND, "NOT_FOUND_PAGE", "더 이상 페이지를 찾을 수 없습니다."),

    NOT_VALID_IMAGE(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "NOT_VALID_IMAGE", "jpg, jpeg, png 형식만 업로드 가능합니다.");

    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
