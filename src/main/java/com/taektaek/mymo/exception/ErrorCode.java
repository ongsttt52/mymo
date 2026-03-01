package com.taektaek.mymo.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    MEMBER_NOT_FOUND("MEMBER_001", "회원을 찾을 수 없습니다."),
    DUPLICATE_USERNAME("MEMBER_002", "이미 사용 중인 사용자명입니다."),
    DUPLICATE_EMAIL("MEMBER_003", "이미 사용 중인 이메일입니다."),

    DAILY_LOG_NOT_FOUND("DAILY_LOG_001", "일일 기록을 찾을 수 없습니다."),
    DUPLICATE_DAILY_LOG_DATE("DAILY_LOG_002", "해당 날짜에 이미 기록이 존재합니다."),

    MEMO_NOT_FOUND("MEMO_001", "메모를 찾을 수 없습니다."),

    PHOTO_LOG_NOT_FOUND("PHOTO_LOG_001", "사진 기록을 찾을 수 없습니다."),

    MUSIC_LOG_NOT_FOUND("MUSIC_LOG_001", "음악 기록을 찾을 수 없습니다."),

    UNAUTHORIZED("AUTH_001", "인증이 필요합니다."),
    INVALID_CREDENTIALS("AUTH_002", "이메일 또는 비밀번호가 올바르지 않습니다."),
    ACCESS_DENIED("AUTH_003", "접근 권한이 없습니다."),

    INVALID_INPUT("COMMON_001", "입력값이 올바르지 않습니다."),
    INTERNAL_SERVER_ERROR("COMMON_002", "서버 내부 오류가 발생했습니다.");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
