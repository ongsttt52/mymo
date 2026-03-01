package com.taektaek.mymo.exception;

public class PhotoLogNotFoundException extends BusinessException {

    public PhotoLogNotFoundException() {
        super(ErrorCode.PHOTO_LOG_NOT_FOUND);
    }
}
