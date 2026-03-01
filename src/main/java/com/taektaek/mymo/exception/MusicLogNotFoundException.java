package com.taektaek.mymo.exception;

public class MusicLogNotFoundException extends BusinessException {

    public MusicLogNotFoundException() {
        super(ErrorCode.MUSIC_LOG_NOT_FOUND);
    }
}
