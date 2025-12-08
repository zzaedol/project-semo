package com.semo.security.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.security.Key;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Log4j2
public class JwtProvider {

  @Value("${com.semo.jwt.secret}")
  private String jwtSecret;

  @Value("${com.semo.jwt.expiration-time}")
  private long jwtExpirationTime;

  private Key key;

  @PostConstruct // 의존성 주입 후 초기화
  public void init() {
    this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes());
  }

  /**
   * JWT 토큰을 생성합니다.
   * @param claims 토큰에 포함될 정보 (예: 이메일, 역할)
   * @return 생성된 JWT 토큰 문자열
   */
  public String generateToken(Map<String, Object> claims) {
    return Jwts.builder()
        .setHeader(Map.of("typ", "JWT")) // JWT 헤더 설정
        .setClaims(claims)
        .setIssuedAt(Date.from(ZonedDateTime.now().toInstant())) // 토큰 발행 시간
        .setExpiration(Date.from(ZonedDateTime.now().plusHours(jwtExpirationTime).toInstant())) // 만료 시간 설정
        .signWith(key) // JWT에 서명
        .compact();
  }

  /**
   * JWT 토큰의 유효성을 검사하고 페이로드(claims)를 추출합니다.
   * @param token 유효성 검사할 JWT 토큰
   * @return 토큰 페이로드 (Claims)
   * @throws JwtException 유효하지 않은 토큰일 경우 예외 발생
   */
  public Jws<Claims> parseToken(String token) {
    try {
      return Jwts.parser()
          .setSigningKey(key)
          .build()
          .parseClaimsJws(token);
    } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
      log.info("잘못된 JWT 서명입니다.");
    } catch (ExpiredJwtException e) {
      log.info("만료된 JWT 토큰입니다.");
    } catch (UnsupportedJwtException e) {
      log.info("지원되지 않는 JWT 토큰입니다.");
    } catch (IllegalArgumentException e) {
      log.info("JWT 토큰이 잘못되었습니다.");
    }
    return null;
  }

  /**
   * JWT 토큰에서 만료 시간을 추출합니다.
   * @param token JWT 토큰
   * @return 토큰의 만료 시간, 유효하지 않은 토큰인 경우 null
   */
  public Date getExpirationFromToken(String token) {
    try {
      Jws<Claims> claims = parseToken(token);
      if (claims != null) {
        return claims.getBody().getExpiration();
      }
    } catch (Exception e) {
      log.warn("토큰에서 만료 시간을 추출하는데 실패했습니다: {}", e.getMessage());
    }
    return null;
  }

  /**
   * JWT 토큰에서 사용자 이메일을 추출합니다.
   * @param token JWT 토큰
   * @return 사용자 이메일, 유효하지 않은 토큰인 경우 null
   */
  public String getEmailFromToken(String token) {
    try {
      Jws<Claims> claims = parseToken(token);
      if (claims != null) {
        return claims.getBody().get("email", String.class);
      }
    } catch (Exception e) {
      log.warn("토큰에서 이메일을 추출하는데 실패했습니다: {}", e.getMessage());
    }
    return null;
  }
}
