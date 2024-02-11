package com.iot.gateway.entity;

import jakarta.persistence.*;
import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.CreationTimestamp;

@Data
@NoArgsConstructor
@ToString
@Entity(name = "refresh_token")
public class RefreshTokenEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  @Column(name = "token")
  private String token;

  @OneToOne(optional = false)
  @JoinColumn(name = "user_id", nullable = false, updatable = false)
  private UserEntity userEntity;

  @Column(name = "expires_at")
  private Instant expiresAt;

  @Column(name = "created_at")
  @CreationTimestamp
  private Instant createdAt;
}
