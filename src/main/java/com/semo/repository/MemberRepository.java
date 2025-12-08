package com.semo.repository;

import com.semo.entity.Member;
import java.util.Optional;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

  /**
   * 이메일을 기준으로 Member 엔티티를 조회합니다.
   * memberRoles와 role을 Eager Loading하여 Lazy Loading 문제를 방지합니다.
   * 회원 가입 시 중복 이메일 체크에 사용됩니다.
   * @param email 조회할 이메일 주소
   * @return Optional<Member> (Member 엔티티 또는 빈 Optional)
   */
  @EntityGraph(attributePaths = {"memberRoles", "memberRoles.role"})
  Optional<Member> findByEmail(String email);

  /**
   * 이메일이 이미 존재하는지 확인합니다.
   * @param email 확인할 이메일 주소
   * @return 이메일 존재 여부 (true/false)
   */
  boolean existsByEmail(String email);
}