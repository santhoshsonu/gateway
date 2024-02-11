package com.iot.gateway.exception;

import java.util.Map;

public class BadRequestException extends GatewayException {
  public BadRequestException(String message, Map<String, String> errors) {
    super(message, 400, errors);
  }
}
