package com.iot.gateway.auth;

import java.util.List;

public class Roles {
  private Roles() {
    throw new IllegalStateException("Roles class");
  }

  public static final String ADMIN = "ADMIN";
  public static final String USER = "USER";

  public static final List<String> ROLE_LIST = List.of(ADMIN, USER);
}
