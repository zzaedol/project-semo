package com.semo.dto.login;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 로그아웃 응답 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LogoutResponseDTO {
    
    /**
     * 로그아웃 성공 여부
     */
    private boolean success;
    
    /**
     * 응답 메시지
     */
    private String message;
    
    /**
     * 로그아웃 처리 시간 (타임스탬프)
     */
    private long timestamp;
    
    /**
     * 성공적인 로그아웃 응답을 생성하는 정적 팩토리 메서드
     */
    public static LogoutResponseDTO success() {
        return LogoutResponseDTO.builder()
            .success(true)
            .message("로그아웃이 성공적으로 처리되었습니다.")
            .timestamp(System.currentTimeMillis())
            .build();
    }
    
    /**
     * 실패한 로그아웃 응답을 생성하는 정적 팩토리 메서드
     */
    public static LogoutResponseDTO failure(String message) {
        return LogoutResponseDTO.builder()
            .success(false)
            .message(message)
            .timestamp(System.currentTimeMillis())
            .build();
    }
}
