package com.semo.repository;

import com.semo.entity.Role;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Role 엔티티의 데이터베이스 접근을 담당하는 Repository 인터페이스입니다.
 * 사용자 권한 정보 조회에 사용됩니다.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

  /**
   * 권한 이름(name)을 기준으로 Role 엔티티를 조회합니다.
   * @param name 조회할 권한 이름 (예: "TENANT", "OWNER")
   * @return Optional<Role> (Role 엔티티 또는 빈 Optional)
   */
  Optional<Role> findByName(String name);
}
