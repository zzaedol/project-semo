package com.semo.service;

import com.semo.dto.login.LoginRequestDTO;
import com.semo.dto.login.LoginResponseDTO;
import com.semo.entity.Member;
import com.semo.mapper.MemberMapper;
import com.semo.security.jwt.JwtProvider;
import com.semo.security.service.CustomerUserDetails;
import java.util.Date;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Log4j2
public class AuthService {

  private final AuthenticationManager authenticationManager;
  private final JwtProvider jwtProvider;
  private final MemberMapper memberMapper; // MapStruct 매퍼 주입
  private final TokenBlacklistService tokenBlacklistService;

  /**
   * 사용자 로그인 처리 메서드
   * @param loginRequestDTO 로그인 요청 DTO (이메일, 비밀번호)
   * @return JWT 토큰 및 사용자 정보가 포함된 응답 DTO
   */
  public LoginResponseDTO login(LoginRequestDTO loginRequestDTO) {
    log.info("로그인 시도: {}", loginRequestDTO.getEmail());

    // Spring Security의 AuthenticationManager를 사용하여 인증 시도
    UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(
        loginRequestDTO.getEmail(),
        loginRequestDTO.getPassword()
    );

    Authentication authentication = authenticationManager.authenticate(authenticationToken);

    // 인증 성공 시 JWT 토큰 생성
    CustomerUserDetails userDetails = (CustomerUserDetails) authentication.getPrincipal();
    Member authenticatedMember = userDetails.getMember(); // CustomerUserDetails의 getMember() 메서드 호출

    Map<String, Object> claims = Map.of(
        "email", authenticatedMember.getEmail(),
        "roles", authenticatedMember.getMemberRoles().stream()
            .map(memberRole -> memberRole.getRole().getName()).toArray()
    );
    String token = jwtProvider.generateToken(claims);

    // MapStruct를 사용하여 Member 엔티티를 DTO로 변환
    LoginResponseDTO responseDTO = memberMapper.toLoginResponseDto(authenticatedMember);
    responseDTO.setToken(token); // 토큰은 매핑에서 제외되었으므로 수동 설정

    return responseDTO;
  }

  /**
   * 사용자 로그아웃 처리 메서드
   * JWT 토큰을 블랙리스트에 추가하여 무효화합니다.
   * @param token 무효화할 JWT 토큰
   * @return 로그아웃 성공 여부
   */
  public boolean logout(String token) {
    try {
      // 토큰 유효성 검사
      if (jwtProvider.parseToken(token) == null) {
        log.warn("유효하지 않은 토큰으로 로그아웃 시도");
        return false;
      }

      // 토큰에서 사용자 정보 추출
      String email = jwtProvider.getEmailFromToken(token);
      Date expiration = jwtProvider.getExpirationFromToken(token);

      if (email == null || expiration == null) {
        log.warn("토큰에서 필요한 정보를 추출할 수 없습니다.");
        return false;
      }

      // 토큰을 블랙리스트에 추가
      tokenBlacklistService.addToBlacklist(token, expiration);
      
      log.info("사용자 {} 로그아웃 완료", email);
      return true;
      
    } catch (Exception e) {
      log.error("로그아웃 처리 중 오류 발생: {}", e.getMessage());
      return false;
    }
  }
}
