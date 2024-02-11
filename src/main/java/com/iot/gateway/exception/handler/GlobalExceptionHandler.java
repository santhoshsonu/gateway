package com.iot.gateway.exception.handler;

import com.iot.gateway.exception.ErrorResponse;
import com.iot.gateway.exception.GatewayException;
import com.iot.gateway.exception.ResourceNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.server.ServerWebInputException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  public static final String ERR_MSG_500 = "Something went wrong";

  public static final ErrorResponse internalServerError = new ErrorResponse(ERR_MSG_500, 500);

  @ExceptionHandler(AccessDeniedException.class)
  public ResponseEntity<ErrorResponse> handleAccessDeniedException(AccessDeniedException e) {
    return ResponseEntity.status(403).body(new ErrorResponse(e.getMessage(), 403));
  }

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleUserNotFoundException(ResourceNotFoundException e) {
    return ResponseEntity.status(404).body(new ErrorResponse(e.getMessage(), e.getStatus()));
  }

  @ExceptionHandler(GatewayException.class)
  public ResponseEntity<ErrorResponse> handleGatewayException(GatewayException e) {
    if (e.getStatus() == 500) {
      log.warn("Exception occurred: {}", e.getMessage());
      return ResponseEntity.status(e.getStatus()).body(internalServerError);
    }
    return ResponseEntity.status(e.getStatus())
        .body(new ErrorResponse(e.getMessage(), e.getStatus(), e.getErrors()));
  }

  @ExceptionHandler(NoResourceFoundException.class)
  public ResponseEntity<ErrorResponse> handleNoResourceFoundException(NoResourceFoundException e) {
    return ResponseEntity.status(404).body(new ErrorResponse(e.getMessage(), 404));
  }

  @ExceptionHandler(ServerWebInputException.class)
  public ResponseEntity<ErrorResponse> handleServerWebInputException(ServerWebInputException e) {
    return ResponseEntity.badRequest().body(new ErrorResponse(e.getMessage(), 400));
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ErrorResponse> handleException(RuntimeException e, WebRequest request) {
    log.error(
        "Unexpected error occurred while calling API {} : {}",
        request.getContextPath(),
        e.getMessage(),
        e);
    return ResponseEntity.status(500).body(internalServerError);
  }
}
