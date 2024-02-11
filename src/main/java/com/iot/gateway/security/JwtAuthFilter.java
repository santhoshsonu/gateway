package com.iot.gateway.security;

import com.iot.gateway.auth.JwtUtils;
import com.iot.gateway.security.services.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {
  public static final String AUTHORIZATION_HEADER = "Authorization";
  public static final String TOKEN_PREFIX = "Bearer ";
  @Autowired JwtUtils jwtUtils;
  @Autowired UserDetailsServiceImpl userDetailsService;

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    final String jwt = parseJwt(request);

    if (StringUtils.hasText(jwt) && jwtUtils.isTokenValid(jwt)) {
      final String username = jwtUtils.getUserNameFromJwtToken(jwt);
      final UserDetails userDetails = userDetailsService.loadUserByUsername(username);

      final UsernamePasswordAuthenticationToken authentication =
          new UsernamePasswordAuthenticationToken(
              userDetails, null, userDetails == null ? List.of() : userDetails.getAuthorities());

      authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
      SecurityContextHolder.getContext().setAuthentication(authentication);
    }
    filterChain.doFilter(request, response);
  }

  private String parseJwt(HttpServletRequest request) {
    final String headerAuth = request.getHeader(AUTHORIZATION_HEADER);
    if (StringUtils.hasText(headerAuth) && headerAuth.startsWith(TOKEN_PREFIX)) {
      return headerAuth.substring(7);
    }
    return null;
  }
}
