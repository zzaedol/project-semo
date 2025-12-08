package com.semo.service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

/**
 * JWT 토큰 블랙리스트 관리 서비스
 * 로그아웃된 토큰들을 메모리에서 관리합니다.
 */
@Service
@Log4j2
public class TokenBlacklistService {

    // 블랙리스트된 토큰과 만료 시간을 저장하는 ConcurrentHashMap
    // Key: 토큰, Value: 만료 시간
    private final ConcurrentHashMap<String, LocalDateTime> blacklistedTokens = new ConcurrentHashMap<>();

    /**
     * 토큰을 블랙리스트에 추가합니다.
     * @param token 블랙리스트에 추가할 JWT 토큰
     * @param expiration 토큰의 만료 시간
     */
    public void addToBlacklist(String token, Date expiration) {
        LocalDateTime expirationDateTime = expiration.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDateTime();
        
        blacklistedTokens.put(token, expirationDateTime);
        log.info("토큰이 블랙리스트에 추가되었습니다. 만료 시간: {}", expirationDateTime);
        
        // 만료된 토큰들을 정리
        cleanupExpiredTokens();
    }

    /**
     * 토큰이 블랙리스트에 있는지 확인합니다.
     * @param token 확인할 JWT 토큰
     * @return 블랙리스트에 있으면 true, 없으면 false
     */
    public boolean isBlacklisted(String token) {
        // 만료된 토큰들을 먼저 정리
        cleanupExpiredTokens();
        
        boolean isBlacklisted = blacklistedTokens.containsKey(token);
        if (isBlacklisted) {
            log.debug("블랙리스트된 토큰 사용 시도");
        }
        return isBlacklisted;
    }

    /**
     * 만료된 토큰들을 블랙리스트에서 제거합니다.
     * 메모리 효율성을 위해 주기적으로 실행됩니다.
     */
    private void cleanupExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        int initialSize = blacklistedTokens.size();
        
        blacklistedTokens.entrySet().removeIf(entry -> 
            entry.getValue().isBefore(now)
        );
        
        int cleanedTokens = initialSize - blacklistedTokens.size();
        if (cleanedTokens > 0) {
            log.debug("만료된 토큰 {} 개가 블랙리스트에서 정리되었습니다.", cleanedTokens);
        }
    }

    /**
     * 현재 블랙리스트에 있는 토큰의 개수를 반환합니다.
     * @return 블랙리스트된 토큰 개수
     */
    public int getBlacklistSize() {
        cleanupExpiredTokens();
        return blacklistedTokens.size();
    }
}
