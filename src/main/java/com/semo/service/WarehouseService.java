package com.semo.service;

import com.semo.dto.member.WarehouseDTO;
import com.semo.dto.member.WarehouseRegisterDTO;
import com.semo.entity.Warehouse;
import java.util.List;

public interface WarehouseService {

  /**
   * 창고 등록
   * @param warehouseRegisterDTO 창고 등록 정보
   * @param ownerId 임대인(소유주) ID
   * @param image 창고 이미지 파일 (선택 사항)
   * @return 등록된 창고 Entity
   */
  Warehouse registerWarehouse(WarehouseRegisterDTO warehouseRegisterDTO, Long ownerId, org.springframework.web.multipart.MultipartFile image);

  /**
   * 모든 창고 목록 조회
   * @return 창고 DTO 목록
   */
  List<WarehouseDTO> getAllWarehouses();

  /**
   * 특정 소유주의 창고 목록 조회
   * @param ownerId 소유주 ID
   * @return 해당 소유주의 창고 DTO 목록
   */
  List<WarehouseDTO> getWarehousesByOwnerId(Long ownerId);

  /**
   * 창고 상세 조회
   * @param warehouseId 창고 ID
   * @return 창고 DTO
   */
  WarehouseDTO getWarehouseById(Long warehouseId);

  /**
   * 창고 수정
   * @param warehouseId 수정할 창고 ID
   * @param warehouseRegisterDTO 수정할 창고 정보
   * @param image 새 이미지 파일 (선택 사항)
   * @return 수정된 창고 DTO
   */
  WarehouseDTO updateWarehouse(Long warehouseId, WarehouseRegisterDTO warehouseRegisterDTO, org.springframework.web.multipart.MultipartFile image);

  /**
   * 창고 삭제
   * @param warehouseId 삭제할 창고 ID
   * @param ownerId 소유주 ID (권한 확인용)
   */
  void deleteWarehouse(Long warehouseId, Long ownerId);
}