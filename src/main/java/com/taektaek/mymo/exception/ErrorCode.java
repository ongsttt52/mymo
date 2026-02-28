package com.taektaek.mymo.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    MEMBER_NOT_FOUND("MEMBER_001", "회원을 찾을 수 없습니다."),
    DUPLICATE_USERNAME("MEMBER_002", "이미 사용 중인 사용자명입니다."),
    DUPLICATE_EMAIL("MEMBER_003", "이미 사용 중인 이메일입니다."),
    INVALID_INPUT("COMMON_001", "입력값이 올바르지 않습니다."),
    INTERNAL_SERVER_ERROR("COMMON_002", "서버 내부 오류가 발생했습니다.");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}
