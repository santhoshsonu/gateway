package com.iot.gateway.auth;

import com.iot.gateway.exception.GatewayException;
import com.iot.gateway.security.services.UserDetailsImpl;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.util.Date;
import java.util.function.Function;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class JwtUtils {

  @Value("${jwt.secret}")
  private String jwtSecret;

  @Value("${jwt.expiration}")
  private Long expiration;

  @Value("${jwt.issuer}")
  private String issuer;

  private SecretKey signKey;

  @PostConstruct
  void init() {
    byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
    this.signKey = Keys.hmacShaKeyFor(keyBytes);
    this.expiration = expiration * 1000;
  }

  public String generateToken(UserDetailsImpl userDetails) {
    return Jwts.builder()
        .subject(userDetails.getUsername())
        .claim("user", userDetails)
        .issuer(issuer)
        .issuedAt(new Date())
        .expiration(new Date(System.currentTimeMillis() + expiration))
        .signWith(signKey)
        .compact();
  }

  public boolean isTokenValid(String jwt) {
    Claims claims;
    try {
      claims = extractAllClaims(jwt);
    } catch (JwtException e) {
      return false;
    }
    return StringUtils.hasText(claims.getSubject())
        && issuer.equals(claims.getIssuer())
        && claims.getExpiration().after(new Date());
  }

  public String getUserNameFromJwtToken(String jwt) {
    return extractClaim(jwt, Claims::getSubject);
  }

  public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
    final Claims claims = extractAllClaims(token);
    return claimsResolver.apply(claims);
  }

  SecretKey getSignKey() {
    if (this.signKey != null) {
      return this.signKey;
    }
    throw new GatewayException("Sign key not found", 500, null);
  }

  private Claims extractAllClaims(String token) throws JwtException {
    return Jwts.parser().verifyWith(getSignKey()).build().parseSignedClaims(token).getPayload();
  }
}
