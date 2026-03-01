package com.taektaek.mymo.exception;

public class DailyLogNotFoundException extends BusinessException {

    public DailyLogNotFoundException() {
        super(ErrorCode.DAILY_LOG_NOT_FOUND);
    }
}
