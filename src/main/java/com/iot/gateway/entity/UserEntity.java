package com.iot.gateway.entity;

import com.iot.gateway.auth.Role;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.Accessors;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Accessors(chain = true)
@Entity(name = "users")
@Table(name = "users")
@SequenceGenerator(name = "user_seq_generator", sequenceName = "seq_user_id", allocationSize = 1)
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
  private String password;

  @Enumerated(EnumType.STRING)
  @Column(name = "role")
  private Role role;
}
