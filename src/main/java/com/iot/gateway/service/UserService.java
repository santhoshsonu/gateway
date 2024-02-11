package com.iot.gateway.service;

import static com.iot.gateway.exception.GatewayException.internalServerException;

import com.iot.gateway.auth.Roles;
import com.iot.gateway.entity.UserEntity;
import com.iot.gateway.exception.BadRequestException;
import com.iot.gateway.exception.GatewayException;
import com.iot.gateway.exception.ResourceNotFoundException;
import com.iot.gateway.mapper.UserMapper;
import com.iot.gateway.model.User;
import com.iot.gateway.repository.UserRepository;
import com.iot.gateway.security.services.UserDetailsImpl;
import com.iot.gateway.validator.GenericValidator;
import jakarta.validation.groups.Default;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class UserService {
  private final UserRepository userRepository;
  private final GenericValidator validator;

  private final PasswordEncoder passwordEncoder;

  @Autowired
  public UserService(
      UserRepository userRepository, GenericValidator validator, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.validator = validator;
    this.passwordEncoder = passwordEncoder;
  }

  @Transactional
  public User getUser(String username) {
    return userRepository
        .findByEmail(username)
        .map(UserMapper::toUser)
        .orElseThrow(
            () -> new ResourceNotFoundException(String.format("User: %s not found", username)));
  }

  @Transactional(readOnly = true)
  public List<User> getUsers() {
    return userRepository.findAll().stream().map(UserMapper::toUser).toList();
  }

  @Transactional
  public User createUser(User user) {
    validateUser(user, true);
    userRepository
        .findByEmail(user.getEmail())
        .ifPresent(
            u -> {
              throw new GatewayException(
                  String.format("User with email: %s already exists", u.getEmail()),
                  HttpStatus.CONFLICT.value(),
                  null);
            });
    final UserEntity entity = UserMapper.toUserEntity(user);
    entity.setPassword(passwordEncoder.encode(user.getPassword()));
    return UserMapper.toUser(saveUser(entity));
  }

  @Transactional(propagation = Propagation.SUPPORTS)
  public UserEntity saveUser(UserEntity entity) {
    try {
      return userRepository.saveAndFlush(entity);
    } catch (Exception e) {
      log.error("Failed to save user: {}", entity.getEmail(), e);
      throw internalServerException;
    }
  }

  @Transactional
  public User getProfile() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();
    return getUser(principal.getUsername());
  }

  @Transactional
  public User updateUser(User user) {
    validateUser(user, false);
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    UserDetailsImpl principal = (UserDetailsImpl) authentication.getPrincipal();

    final boolean isAdmin =
        principal.getAuthorities().stream()
            .anyMatch(grantedAuthority -> Roles.ADMIN.equals(grantedAuthority.getAuthority()));

    if (principal.getUsername().equals(user.getEmail()) || isAdmin) {
      final UserEntity userEntity =
          userRepository
              .findByEmail(user.getEmail())
              .orElseThrow(() -> new ResourceNotFoundException("User not found"));

      UserMapper.updateEntity(user, userEntity);
      // Only Admin can change role
      if (isAdmin && user.getRole() != null && Roles.ROLE_LIST.contains(user.getRole())) {
        userEntity.setRole(user.getRole());
      }
      saveUser(userEntity);
      return UserMapper.toUser(userEntity);
    }

    throw new GatewayException(
        String.format("Unauthorized to update user with email: %s", user.getEmail()),
        HttpStatus.FORBIDDEN.value(),
        null);
  }

  private void validateUser(User user, boolean creation) {
    if (creation) {
      validator.validate(user, User.ValidationGroups.Create.class, Default.class);
    } else {
      validator.validate(user);
    }
    if (!Roles.ROLE_LIST.contains(user.getRole())) {
      throw new BadRequestException(
          "Invalid data", Map.of("role", String.format("Invalid role: %s", user.getRole())));
    }
  }
}
