package com.iot.gateway.security.services;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.iot.gateway.model.User;
import java.util.Collection;
import java.util.List;
import lombok.Getter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@ToString
public class UserDetailsImpl implements UserDetails {
  private final String username;
  private final String firstname;
  private final String lastname;
  @JsonIgnore @ToString.Exclude private final String password;
  @JsonIgnore private final Collection<? extends GrantedAuthority> authorities;

  public UserDetailsImpl(User user) {
    this.username = user.getEmail();
    this.firstname = user.getFirstname();
    this.lastname = user.getLastname();
    this.password = user.getPassword();
    this.authorities = List.of(new SimpleGrantedAuthority(user.getRole()));
  }

  @Override
  @JsonIgnore
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  @JsonIgnore
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  @JsonIgnore
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  @JsonIgnore
  public boolean isEnabled() {
    return true;
  }
}
