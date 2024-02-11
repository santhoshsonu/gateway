package com.iot.gateway.exception;

import java.util.Map;
import lombok.Getter;

@Getter
public class GatewayException extends RuntimeException {
  private final int status;
  private final String message;
  private final Map<String, String> errors;

  public GatewayException(String message, int status, Map<String, String> errors) {
    this.message = message;
    this.status = status;
    this.errors = errors;
  }

  public static final GatewayException internalServerException =
      new GatewayException("Internal server error", 500, null);
}
