package com.semo.security.filter;

import com.semo.security.jwt.JwtProvider;
import com.semo.security.service.impl.CustomerUserDetailsService;
import com.semo.service.TokenBlacklistService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

@Log4j2
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

  private final JwtProvider jwtProvider;
  private final CustomerUserDetailsService customerUserDetailsService;
  private final TokenBlacklistService tokenBlacklistService;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    try {
      String token = resolveToken(request);
      if (token != null && jwtProvider.parseToken(token) != null) {
        // 토큰이 블랙리스트에 있는지 확인
        if (tokenBlacklistService.isBlacklisted(token)) {
          log.warn("블랙리스트된 토큰으로 접근 시도");
          filterChain.doFilter(request, response);
          return;
        }

        // 토큰에서 이메일 추출
        String email = (String) jwtProvider.parseToken(token).getBody().get("email");

        // UserDetailsService를 통해 사용자 정보 로드
        UserDetails userDetails = customerUserDetailsService.loadUserByUsername(email);

        // 인증 객체 생성 및 SecurityContextHolder에 설정
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
            userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    } catch (Exception e) {
      log.error("인증 필터에서 오류 발생: {}", e.getMessage());
    }

    filterChain.doFilter(request, response);
  }

  /**
   * HTTP 요청 헤더에서 JWT 토큰을 추출합니다.
   */
  private String resolveToken(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7); // "Bearer " 제거
    }
    return null;
  }
}
