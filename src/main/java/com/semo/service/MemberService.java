package com.semo.service;

import com.semo.dto.member.MemberSignupDTO;

/**
 * 회원 관련 비즈니스 로직을 처리하는 서비스 인터페이스입니다.
 */
public interface MemberService {

  /**
   * 회원가입을 처리하고 관련 정보를 저장합니다.
   * @param signupDTO 회원가입에 필요한 모든 정보를 담은 DTO
   * @throws IllegalStateException 이메일 중복 또는 유효성 검증 실패 시 발생
   */
  void signup(MemberSignupDTO signupDTO);
}
