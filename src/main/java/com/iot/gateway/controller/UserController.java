package com.iot.gateway.controller;

import com.iot.gateway.auth.Roles;
import com.iot.gateway.model.User;
import com.iot.gateway.service.UserService;
import jakarta.annotation.security.RolesAllowed;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

  private final UserService userService;

  @Autowired
  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/")
  @RolesAllowed(Roles.ADMIN)
  public ResponseEntity<List<User>> getUsers() {
    return ResponseEntity.ok(userService.getUsers());
  }

  @PostMapping("/create")
  @RolesAllowed(Roles.ADMIN)
  public ResponseEntity<User> createUser(@RequestBody User user) {
    return ResponseEntity.ok(userService.createUser(user));
  }

  @GetMapping("/profile")
  public ResponseEntity<User> getProfile() {
    return ResponseEntity.ok(userService.getProfile());
  }
}
