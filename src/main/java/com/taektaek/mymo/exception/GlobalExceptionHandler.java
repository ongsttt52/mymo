package com.taektaek.mymo.exception;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @ExceptionHandler(MemberNotFoundException.class)
  protected ResponseEntity<ErrorResponse> handleMemberNotFoundException(MemberNotFoundException e) {
    ErrorResponse response = ErrorResponse.of(e.getErrorCode());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
  }

  @ExceptionHandler(DailyLogNotFoundException.class)
  protected ResponseEntity<ErrorResponse> handleDailyLogNotFoundException(
      DailyLogNotFoundException e) {
    ErrorResponse response = ErrorResponse.of(e.getErrorCode());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
  }

  @ExceptionHandler(MemoNotFoundException.class)
  protected ResponseEntity<ErrorResponse> handleMemoNotFoundException(MemoNotFoundException e) {
    ErrorResponse response = ErrorResponse.of(e.getErrorCode());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
  }

  @ExceptionHandler(PhotoLogNotFoundException.class)
  protected ResponseEntity<ErrorResponse> handlePhotoLogNotFoundException(
      PhotoLogNotFoundException e) {
    ErrorResponse response = ErrorResponse.of(e.getErrorCode());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
  }

  @ExceptionHandler(MusicLogNotFoundException.class)
  protected ResponseEntity<ErrorResponse> handleMusicLogNotFoundException(
      MusicLogNotFoundException e) {
    ErrorResponse response = ErrorResponse.of(e.getErrorCode());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
  }

  @ExceptionHandler(DuplicateMemberException.class)
  protected ResponseEntity<ErrorResponse> handleDuplicateMemberException(
      DuplicateMemberException e) {
    ErrorResponse response = ErrorResponse.of(e.getErrorCode());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
  }

  @ExceptionHandler(DuplicateDailyLogDateException.class)
  protected ResponseEntity<ErrorResponse> handleDuplicateDailyLogDateException(
      DuplicateDailyLogDateException e) {
    ErrorResponse response = ErrorResponse.of(e.getErrorCode());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
  }

  @ExceptionHandler(InvalidCredentialsException.class)
  protected ResponseEntity<ErrorResponse> handleInvalidCredentialsException(
      InvalidCredentialsException e) {
    ErrorResponse response = ErrorResponse.of(e.getErrorCode());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
  }

  @ExceptionHandler(ResourceAccessDeniedException.class)
  protected ResponseEntity<ErrorResponse> handleResourceAccessDeniedException(
      ResourceAccessDeniedException e) {
    ErrorResponse response = ErrorResponse.of(e.getErrorCode());
    return ResponseEntity.status(HttpStatus.FORBIDDEN).body(response);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  protected ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException e) {
    List<ErrorResponse.FieldError> fieldErrors =
        e.getBindingResult().getFieldErrors().stream()
            .map(
                error ->
                    new ErrorResponse.FieldError(
                        error.getField(),
                        error.getRejectedValue() == null ? "" : error.getRejectedValue().toString(),
                        error.getDefaultMessage()))
            .toList();
    ErrorResponse response = ErrorResponse.of(ErrorCode.INVALID_INPUT, fieldErrors);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
  }

  @ExceptionHandler(Exception.class)
  protected ResponseEntity<ErrorResponse> handleException(Exception e) {
    log.error("Unhandled exception", e);
    ErrorResponse response = ErrorResponse.of(ErrorCode.INTERNAL_SERVER_ERROR);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
  }
}
