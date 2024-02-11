package com.iot.gateway.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import lombok.experimental.Accessors;

@Getter
@Setter
@ToString
@NoArgsConstructor
@Accessors(chain = true)
public class User {
  private Long id;

  @NotNull(message = "email must not be null")
  @Email(message = "email must be a valid email address")
  private String email;

  @NotNull(message = "firstname must not be null")
  private String firstname;

  @NotNull(message = "lastname must not be null")
  private String lastname;

  @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
  @NotNull(message = "password must not be null", groups = ValidationGroups.Create.class)
  @ToString.Exclude
  private String password;

  @NotNull(message = "role must not be null")
  private String role;

  public interface ValidationGroups {
    interface Create {}
  }
}
