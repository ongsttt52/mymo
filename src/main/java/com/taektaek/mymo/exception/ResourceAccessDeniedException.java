package com.taektaek.mymo.exception;

public class ResourceAccessDeniedException extends BusinessException {

  public ResourceAccessDeniedException() {
    super(ErrorCode.ACCESS_DENIED);
  }
}
