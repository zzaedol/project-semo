package com.semo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass // 상위 클래스의 필드를 하위 엔티티 클래스의 매핑 정보로 포함합니다.
@EntityListeners(value = {AuditingEntityListener.class}) // Auditing 기능을 사용하기 위한 리스너를 설정합니다.
@Getter
public abstract class BasicEntity {

  @CreatedDate
  @Column(name = "created_at", updatable = false)
  private LocalDateTime createdAt; // 엔티티 생성 시 자동 저장

  @LastModifiedDate
  @Column(name = "updated_at")
  private LocalDateTime updatedAt; // 엔티티 수정 시 자동 저장
}
