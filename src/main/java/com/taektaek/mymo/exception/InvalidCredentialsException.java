package com.taektaek.mymo.exception;

public class InvalidCredentialsException extends BusinessException {

  public InvalidCredentialsException() {
    super(ErrorCode.INVALID_CREDENTIALS);
  }
}
