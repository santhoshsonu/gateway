package com.iot.gateway.security.services;

import static com.iot.gateway.exception.GatewayException.internalServerException;

import com.iot.gateway.entity.RefreshTokenEntity;
import com.iot.gateway.entity.UserEntity;
import com.iot.gateway.exception.ResourceNotFoundException;
import com.iot.gateway.repository.RefreshTokenRepository;
import com.iot.gateway.repository.UserRepository;
import jakarta.annotation.PostConstruct;
import java.time.Instant;
import java.util.UUID;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class RefreshTokenService {

  @Value("${jwt.refresh-token-expiration}")
  private Long refreshTokenDurationMs;

  private final RefreshTokenRepository refreshTokenRepository;
  private final UserRepository userRepository;

  @Autowired
  public RefreshTokenService(
      RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
    this.refreshTokenRepository = refreshTokenRepository;
    this.userRepository = userRepository;
  }

  @PostConstruct
  public void init() {
    refreshTokenDurationMs *= 1000;
  }

  @Transactional(readOnly = true)
  public RefreshTokenEntity getRefreshToken(String token) {
    return refreshTokenRepository
        .findByToken(token)
        .orElseThrow(() -> new ResourceNotFoundException("Refresh token not found"));
  }

  @Transactional
  public RefreshTokenEntity createRefreshToken(String email) {
    UserEntity userEntity = getUserEntity(email);

    try {
      final RefreshTokenEntity refreshToken = new RefreshTokenEntity();
      refreshToken.setUserEntity(userEntity);
      refreshToken.setToken(UUID.randomUUID().toString());
      refreshToken.setExpiresAt(Instant.now().plusMillis(refreshTokenDurationMs));
      return refreshTokenRepository.saveAndFlush(refreshToken);
    } catch (Exception e) {
      log.error("Failed to create refresh token for user: {}", email);
      throw internalServerException;
    }
  }

  @Transactional
  public void deleteRefreshToken(String username) {
    UserEntity userEntity = getUserEntity(username);
    refreshTokenRepository.deleteByUserEntity(userEntity);
  }

  public boolean isTokenExpired(RefreshTokenEntity refreshTokenEntity) {
    final Instant expiresAt = refreshTokenEntity.getExpiresAt();
    return expiresAt == null || expiresAt.isBefore(Instant.now());
  }

  protected UserEntity getUserEntity(String email) {
    return userRepository
        .findByEmail(email)
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
  }
}
