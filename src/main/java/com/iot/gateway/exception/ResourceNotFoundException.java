package com.iot.gateway.exception;

public class ResourceNotFoundException extends GatewayException {
  public ResourceNotFoundException(String message) {
    super(message, 404, null);
  }
}
