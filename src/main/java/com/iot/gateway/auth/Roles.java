package com.iot.gateway.auth;

public class Roles {
  private Roles() {
    throw new IllegalStateException("Roles class");
  }

  public static final String ADMIN = "ADMIN";
  public static final String USER = "USER";
}
