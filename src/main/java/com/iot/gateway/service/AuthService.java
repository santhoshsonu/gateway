package com.iot.gateway.service;

import com.iot.gateway.auth.JwtUtils;
import com.iot.gateway.dto.AuthRequestDTO;
import com.iot.gateway.dto.JwtResponseDTO;
import com.iot.gateway.dto.TokenRefreshRequest;
import com.iot.gateway.entity.RefreshTokenEntity;
import com.iot.gateway.exception.GatewayException;
import com.iot.gateway.mapper.UserMapper;
import com.iot.gateway.model.User;
import com.iot.gateway.security.services.RefreshTokenService;
import com.iot.gateway.security.services.UserDetailsImpl;
import com.iot.gateway.validator.GenericValidator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class AuthService {
  private final AuthenticationManager authenticationManager;
  private final JwtUtils jwtUtils;
  private final RefreshTokenService refreshTokenService;
  private final GenericValidator validator;

  public AuthService(
      AuthenticationManager authenticationManager,
      JwtUtils jwtUtils,
      RefreshTokenService refreshTokenService,
      GenericValidator validator) {
    this.authenticationManager = authenticationManager;
    this.jwtUtils = jwtUtils;
    this.refreshTokenService = refreshTokenService;
    this.validator = validator;
  }

  public JwtResponseDTO login(AuthRequestDTO authRequestDTO) {
    validator.validate(authRequestDTO);
    try {
      final Authentication authentication =
          authenticationManager.authenticate(
              new UsernamePasswordAuthenticationToken(
                  authRequestDTO.username(), authRequestDTO.password()));

      final UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
      final RefreshTokenEntity refreshTokenEntity =
          refreshTokenService.createRefreshToken(userDetails.getUsername());
      return new JwtResponseDTO(jwtUtils.generateToken(userDetails), refreshTokenEntity.getToken());
    } catch (AuthenticationException e) {
      log.warn("Failed to authenticate user: {}", authRequestDTO.username());
    }
    throw new GatewayException(
        "Failed to authenticate user", HttpStatus.UNAUTHORIZED.value(), null);
  }

  public JwtResponseDTO renewToken(TokenRefreshRequest tokenRefreshRequest) {
    validator.validate(tokenRefreshRequest);

    final RefreshTokenEntity refreshTokenEntity =
        refreshTokenService.getRefreshToken(tokenRefreshRequest.refreshToken());

    if (refreshTokenService.isTokenExpired(refreshTokenEntity)) {
      throw new GatewayException(
          "Refresh token has expired", HttpStatus.UNAUTHORIZED.value(), null);
    }

    final User user = UserMapper.toUser(refreshTokenEntity.getUserEntity());
    final UserDetailsImpl userDetails = new UserDetailsImpl(user);

    return new JwtResponseDTO(jwtUtils.generateToken(userDetails), refreshTokenEntity.getToken());
  }

  public void logout() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null && authentication.isAuthenticated()) {
      var principal = authentication.getPrincipal();
      if (principal instanceof UserDetailsImpl userDetails) {
        refreshTokenService.deleteRefreshToken(userDetails.getUsername());
      }
    }
  }
}
