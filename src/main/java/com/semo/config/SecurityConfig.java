package com.semo.config;

import com.semo.security.filter.JwtAuthenticationFilter;
import com.semo.security.jwt.JwtProvider;
import com.semo.security.service.impl.CustomerUserDetailsService;
import com.semo.service.TokenBlacklistService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.argon2.Argon2PasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 설정을 담당하는 클래스입니다.
 * JWT 기반의 Stateless(무상태) 인증 방식을 구현합니다.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

  private final JwtProvider jwtProvider;
  private final CustomerUserDetailsService customerUserDetailsService;
  private final TokenBlacklistService tokenBlacklistService;

  private static final String[] PUBLIC_RESOURCES = {
      "/css/**"
      , "/js/**"
      , "/images/**"
      , "/webjars/**"
      , "/favicon.ico"
  };

  /**
   * 보안 필터 체인을 설정하는 Bean 메서드입니다.
   * HTTP 요청에 대한 보안 규칙을 정의합니다.
   *
   * @param http HttpSecurity 객체
   * @return 설정된 SecurityFilterChain
   * @throws Exception 보안 설정 중 발생하는 예외
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        // CSRF(Cross-Site Request Forgery) 보호를 비활성화합니다.
        // REST API 서버는 세션을 사용하지 않으므로 CSRF 공격에 대한 위험이 적습니다.
        .csrf(AbstractHttpConfigurer::disable)
        // H2 Console을 위한 frame 허용 (개발 환경에서만)
        .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
        // 세션 관리 정책을 STATELESS(무상태)로 설정합니다.
        // 서버에 사용자 상태를 저장하지 않고, JWT 토큰으로 인증을 처리합니다.
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        // HTTP 요청에 대한 인가 규칙을 설정합니다.
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers(PUBLIC_RESOURCES).permitAll()
            // H2-Console에 대한 접근을 모두 허용합니다. (개발 환경에서만)
            .requestMatchers("/h2-console/**").permitAll()
            // 로그인 및 회원가입 API는 인증 없이 접근을 허용합니다.
            .requestMatchers("/api/member/login", "/api/member/register").permitAll()
            // 로그아웃 API는 인증된 사용자만 접근 가능합니다.
            //.requestMatchers("/api/member/logout").authenticated()
            // 로그인 페이지는 '익명 사용자'만 접근 가능하도록 제한합니다.
            .requestMatchers("/main/login").anonymous()
            // 그 외 '/api'로 시작하는 모든 요청은 인증된 사용자만 접근 가능
            //.requestMatchers("/api/**").authenticated()
            // 다른 모든 요청은 자유롭게 접근을 허용
            .anyRequest().permitAll()
        )
        // JWT 인증 필터를 UsernamePasswordAuthenticationFilter 이전에 추가
        // 모든 요청에 대해 JWT 토큰을 검사하고 유효하면 인증 정보를 SecurityContext에 저장
        .addFilterBefore(new JwtAuthenticationFilter(jwtProvider, customerUserDetailsService, tokenBlacklistService), UsernamePasswordAuthenticationFilter.class)
        // 폼 기반 로그인 기능을 비활성화
        .formLogin(AbstractHttpConfigurer::disable)
        // HTTP Basic 인증 기능을 비활성화
        .httpBasic(AbstractHttpConfigurer::disable)
    ;

    return http.build();
  }

  /**
   * 비밀번호를 안전하게 암호화하기 위한 PasswordEncoder Bean을 등록
   * Argon2 알고리즘을 사용
   *
   * @return Argon2PasswordEncoder 객체
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return Argon2PasswordEncoder.defaultsForSpringSecurity_v5_8();
  }

  /**
   * 인증 처리를 담당하는 AuthenticationManager Bean을 등록
   * DaoAuthenticationProvider를 사용하여 사용자 인증을 처리
   *
   * @return AuthenticationManager 객체
   */
  @Bean
  public AuthenticationManager authenticationManager() {
    DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
    provider.setPasswordEncoder(passwordEncoder()); // 비밀번호 인코더 설정
    provider.setUserDetailsService(customerUserDetailsService); // 사용자 정보 로드 서비스 설정
    return new ProviderManager(provider);
  }
}