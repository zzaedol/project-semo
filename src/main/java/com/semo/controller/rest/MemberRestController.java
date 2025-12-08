package com.semo.controller.rest;

import com.semo.dto.member.MemberSignupDTO;
import com.semo.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 회원 관련 요청을 처리하는 REST API 컨트롤러입니다.
 * 클라이언트(JS fetch)에게 JSON 응답과 HTTP 상태 코드를 반환합니다.
 */
@RestController // @Controller + @ResponseBody 효과
@RequestMapping("/member")
@RequiredArgsConstructor
@Log4j2
public class MemberRestController {

  // MemberService 주입
  private final MemberService memberService;

  /**
   * 회원가입 요청을 처리하고 결과를 JSON 형태로 반환합니다.
   * MultipartFile을 포함하는 MemberSignupDTO를 @ModelAttribute로 받습니다.
   * @param signupDTO 회원가입에 필요한 모든 정보를 담은 DTO
   * @return 가입 성공 시 HTTP 201 Created (혹은 200 OK), 실패 시 400 Bad Request와 에러 메시지를 반환
   */
  @PostMapping("/signup")
  public ResponseEntity<?> processSignup(@ModelAttribute MemberSignupDTO signupDTO) {
    log.info("Received signup request: {}", signupDTO);

    try {
      // 1. 서비스 호출: 회원 및 관련 프로필, 창고 정보 저장
      memberService.signup(signupDTO);

      // 2. 성공 응답 반환: HTTP 201 Created
      // 프론트엔드 JS는 이 201 코드를 받고 Step 5로 전환합니다.
      return ResponseEntity.status(HttpStatus.CREATED).body("회원 가입이 성공적으로 완료되었습니다.");

    } catch (IllegalStateException e) {
      // 3. 실패 응답 반환: HTTP 400 Bad Request (비즈니스 로직 오류: 예. 이메일 중복)
      log.error("Signup failed: {}", e.getMessage());
      return ResponseEntity.badRequest().body(e.getMessage());

    } catch (Exception e) {
      // 4. 서버 오류 응답 반환: HTTP 500 Internal Server Error (예상치 못한 오류)
      log.error("An unexpected error occurred during signup", e);
      return ResponseEntity.internalServerError().body("서버 내부 오류로 인해 가입에 실패했습니다.");
    }
  }
}
