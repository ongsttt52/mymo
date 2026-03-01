package com.taektaek.mymo.exception;

public class DuplicateDailyLogDateException extends BusinessException {

    public DuplicateDailyLogDateException() {
        super(ErrorCode.DUPLICATE_DAILY_LOG_DATE);
    }
}
