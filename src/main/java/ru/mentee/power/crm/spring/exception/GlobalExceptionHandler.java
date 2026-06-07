package ru.mentee.power.crm.spring.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  @Override
  protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers,
      HttpStatusCode status, WebRequest request) {

    Map<String, String> fieldErrors = new HashMap<>();
    List<FieldError> fieldErrorList = ex.getBindingResult().getFieldErrors();
    for (FieldError fieldError : fieldErrorList) {
      fieldErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
    }
    ErrorResponse responseBody = new ErrorResponse(java.time.LocalDateTime.now(), status.value(), "Bad Request",
        "Validation failed", request.getDescription(false).replace("uri=", ""), fieldErrors);

    return ResponseEntity.badRequest().body(responseBody);
  }

  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException ex, WebRequest request) {
    ErrorResponse errorResponse = new ErrorResponse(java.time.LocalDateTime.now(), 404, "Not Found", ex.getMessage(),
        request.getDescription(false).substring(4), null);

    log.warn("Entity not found: {}", ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleGenericException(Exception ex, WebRequest request) {
    log.error(ex.toString(), ex);
    ErrorResponse errorResponse = new ErrorResponse(java.time.LocalDateTime.now(), 500, "Server Failure",
        "An unexpected error occurred", request.getDescription(false).substring(4), null);

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
  }
}
