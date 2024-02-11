package com.iot.gateway.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(String message, int code, Map<String, String> errors) {
  public ErrorResponse(String message, int code) {
    this(message, code, null);
  }
}
