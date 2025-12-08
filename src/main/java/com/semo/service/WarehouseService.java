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
}