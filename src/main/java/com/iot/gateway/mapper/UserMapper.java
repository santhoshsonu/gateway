package com.iot.gateway.mapper;

import com.iot.gateway.entity.UserEntity;
import com.iot.gateway.model.User;

public class UserMapper {

  private UserMapper() {
    throw new IllegalStateException("Utility class");
  }

  public static User toUser(UserEntity entity) {
    return new User()
        .setId(entity.getId())
        .setEmail(entity.getEmail())
        .setFirstname(entity.getFirstname())
        .setLastname(entity.getLastname())
        .setPassword(entity.getPassword())
        .setRole(entity.getRole());
  }

  public static UserEntity toUserEntity(User user) {
    final UserEntity entity = new UserEntity().setEmail(user.getEmail()).setRole(user.getRole());
    updateEntity(user, entity);
    return entity;
  }

  public static void updateEntity(User user, UserEntity entity) {
    entity.setFirstname(user.getFirstname()).setLastname(user.getLastname());
  }
}
