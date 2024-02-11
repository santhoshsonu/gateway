package com.iot.gateway.controller;

import com.iot.gateway.dto.AuthRequestDTO;
import com.iot.gateway.dto.JwtResponseDTO;
import com.iot.gateway.dto.TokenRefreshRequest;
import com.iot.gateway.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

  private final AuthService authService;

  @Autowired
  public AuthController(AuthService authService) {
    this.authService = authService;
  }

  @PostMapping("/token")
  public ResponseEntity<JwtResponseDTO> login(@RequestBody AuthRequestDTO authRequestDTO) {
    return ResponseEntity.ok(authService.login(authRequestDTO));
  }

  @PostMapping("/refresh")
  public ResponseEntity<JwtResponseDTO> refresh(
      @RequestBody TokenRefreshRequest tokenRefreshRequest) {
    return ResponseEntity.ok(authService.renewToken(tokenRefreshRequest));
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout() {
    authService.logout();
    return ResponseEntity.noContent().build();
  }
}
