package com.taektaek.mymo.exception;

public class MemoNotFoundException extends BusinessException {

  public MemoNotFoundException() {
    super(ErrorCode.MEMO_NOT_FOUND);
  }
}
