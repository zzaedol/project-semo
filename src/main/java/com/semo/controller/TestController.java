package com.semo.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * JWT 토큰 검증 및 로그아웃 테스트를 위한 컨트롤러
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/test")
@Log4j2
public class TestController {

    /**
     * 인증이 필요한 테스트 엔드포인트
     * 로그아웃 후 이 엔드포인트에 접근하면 401 Unauthorized가 반환되어야 합니다.
     */
    @GetMapping("/protected")
    public ResponseEntity<String> protectedEndpoint() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            log.info("인증된 사용자 {} 가 보호된 리소스에 접근했습니다.", username);
            return ResponseEntity.ok("안녕하세요, " + username + "님! 이것은 보호된 리소스입니다.");
        }
        
        return ResponseEntity.status(401).body("인증이 필요합니다.");
    }

    /**
     * 현재 인증된 사용자 정보 반환
     */
    @GetMapping("/me")
    public ResponseEntity<String> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            String authorities = authentication.getAuthorities().toString();
            
            return ResponseEntity.ok(String.format(
                "사용자명: %s, 권한: %s", username, authorities
            ));
        }
        
        return ResponseEntity.status(401).body("인증되지 않은 사용자입니다.");
    }
}
