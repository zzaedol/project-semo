package com.semo.controller;

import com.semo.dto.login.LoginRequestDTO;
import com.semo.dto.login.LoginResponseDTO;
import com.semo.dto.login.LogoutResponseDTO;
import com.semo.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
@Log4j2
public class AuthController {

  private final AuthService authService;

  @PostMapping("/login")
  public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
    try {
      LoginResponseDTO responseDTO = authService.login(loginRequestDTO);
      return ResponseEntity.ok(responseDTO);
    } catch (BadCredentialsException e) {
      log.warn("로그인 실패: 잘못된 이메일 또는 비밀번호");
      // 보안을 위해 상세한 에러 메시지를 노출하지 않습니다.
      return ResponseEntity.status(401).build();
    }
  }

  @PostMapping("/logout")
  public ResponseEntity<LogoutResponseDTO> logout(HttpServletRequest request) {
    try {
      // Authorization 헤더에서 JWT 토큰 추출
      String token = extractTokenFromRequest(request);
      
      if (token == null) {
        log.warn("로그아웃 요청에 토큰이 없습니다.");
        return ResponseEntity.badRequest().body(
            LogoutResponseDTO.failure("토큰이 필요합니다.")
        );
      }

      boolean logoutSuccess = authService.logout(token);
      log.info("logoutSuccess >>>>: {}", logoutSuccess);
      
      if (logoutSuccess) {
        return ResponseEntity.ok(LogoutResponseDTO.success());
      } else {
        return ResponseEntity.badRequest().body(
            LogoutResponseDTO.failure("로그아웃 처리에 실패했습니다.")
        );
      }
      
    } catch (Exception e) {
      log.error("로그아웃 처리 중 오류 발생: {}", e.getMessage());
      return ResponseEntity.internalServerError().body(
          LogoutResponseDTO.failure("서버 오류가 발생했습니다.")
      );
    }
  }

  /**
   * HTTP 요청에서 JWT 토큰을 추출합니다.
   * @param request HTTP 요청 객체
   * @return 추출된 JWT 토큰, 없으면 null
   */
  private String extractTokenFromRequest(HttpServletRequest request) {
    String bearerToken = request.getHeader("Authorization");
    if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
      return bearerToken.substring(7); // "Bearer " 제거
    }
    return null;
  }
}