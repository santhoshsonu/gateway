package com.iot.gateway.repository;

import com.iot.gateway.entity.RefreshTokenEntity;
import com.iot.gateway.entity.UserEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
  Optional<RefreshTokenEntity> findByToken(String token);

  @Modifying
  int deleteByUserEntity(UserEntity userEntity);
}
