package com.iot.gateway.security.services;

import com.iot.gateway.exception.ResourceNotFoundException;
import com.iot.gateway.model.User;
import com.iot.gateway.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
  @Autowired UserService userService;

  @Override
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    try {
      final User user = userService.getUser(username);
      return new UserDetailsImpl(user);
    } catch (ResourceNotFoundException e) {
      throw new UsernameNotFoundException(e.getMessage());
    }
  }
}
