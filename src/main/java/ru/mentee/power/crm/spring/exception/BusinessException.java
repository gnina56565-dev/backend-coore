package ru.mentee.power.crm.spring.exception;

import org.springframework.http.HttpStatus;

public class BusinessException extends RuntimeException {

  private final HttpStatus status;

  public BusinessException(HttpStatus status, String message) {
    super(message);
    this.status = status;
  }

  public BusinessException(String message) {
    this(HttpStatus.INTERNAL_SERVER_ERROR, message);
  }

  public BusinessException(String message, Throwable cause) {
    super(message, cause);
    this.status = HttpStatus.INTERNAL_SERVER_ERROR;
  }

  public HttpStatus getStatus() {
    return status;
  }
}
