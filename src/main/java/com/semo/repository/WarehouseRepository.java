package com.semo.repository;

import com.semo.entity.Warehouse;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Warehouse 엔티티의 데이터베이스 접근을 담당하는 Repository 인터페이스입니다.
 * Spring Data JPA를 사용하여 기본적인 CRUD 및 쿼리 기능을 상속받습니다.
 *
 * 이 Repository는 소유주(Owner)가 등록한 창고 정보를 저장, 조회, 수정, 삭제하는 데 사용됩니다.
 */
@Repository
public interface WarehouseRepository extends JpaRepository<Warehouse, Long> {

  // JpaRepository<Warehouse, Long>을 상속받으므로,
  // save(), findById(), findAll() 등의 기본 메서드를 별도 구현 없이 사용할 수 있습니다.

  /**
   * 특정 소유주(Member)가 등록한 모든 창고를 조회합니다.
   * @param ownerId 소유주 Member의 ID
   * @return 해당 소유주가 등록한 Warehouse Entity 목록
   */
  List<Warehouse> findByOwnerId(Long ownerId);
}
