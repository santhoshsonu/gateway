package com.iot.gateway.entity;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Accessors(chain = true)
@Entity(name = "users")
@Table(name = "users")
@SequenceGenerator(name = "user_seq_generator", sequenceName = "seq_user_id", allocationSize = 1)
@Cacheable
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class UserEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq_generator")
  @Column(name = "id")
  private Long id;

  @Column(name = "email")
  private String email;

  @Column(name = "firstname")
  private String firstname;

  @Column(name = "lastname")
  private String lastname;

  @Column(name = "password")
  @ToString.Exclude
  private String password;

  @Column(name = "role")
  private String role;
}
