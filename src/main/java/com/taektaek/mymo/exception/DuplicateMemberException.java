package com.taektaek.mymo.exception;

public class DuplicateMemberException extends BusinessException {

    public DuplicateMemberException(ErrorCode errorCode) {
        super(errorCode);
    }
}
