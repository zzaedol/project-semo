package com.semo.controller.rest;

import com.semo.dto.member.WarehouseDTO;
import com.semo.dto.member.WarehouseRegisterDTO;
import com.semo.entity.Warehouse;
import com.semo.security.service.CustomerUserDetails;
import com.semo.service.WarehouseService;
import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/warehouse")
@RequiredArgsConstructor
@Slf4j
public class WarehouseRestController {

  private final WarehouseService warehouseService;

  @PostMapping(value = "/register", consumes = {"multipart/form-data"})
  public ResponseEntity<?> registerWarehouse(
      @RequestPart("warehouseData") @Valid WarehouseRegisterDTO warehouseRegisterDTO,
      @RequestPart(value = "image", required = false) org.springframework.web.multipart.MultipartFile image) {
    try {
      log.info("창고 등록 API 호출 - 창고명: {}", warehouseRegisterDTO.getTitle());
      if (image != null) {
        log.info("이미지 파일: {}, 크기: {} bytes", image.getOriginalFilename(), image.getSize());
      }

      // 현재 로그인한 사용자 정보 가져오기
      Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

      Long ownerId;
      if (authentication == null || !authentication.isAuthenticated() ||
          authentication.getPrincipal().equals("anonymousUser")) {
        log.error("인증되지 않은 사용자의 창고 등록 시도");
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("message", "로그인이 필요합니다.");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
      } else {
        // JWT 토큰에서 사용자 ID 추출
        CustomerUserDetails userDetails = (CustomerUserDetails) authentication.getPrincipal();
        ownerId = userDetails.getMemberId();
        log.info("로그인 사용자 ID: {}", ownerId);
      }

      // 창고 등록 (이미지 포함)
      Warehouse warehouse = warehouseService.registerWarehouse(warehouseRegisterDTO, ownerId, image);

      // 응답 데이터 생성
      Map<String, Object> response = new HashMap<>();
      response.put("success", true);
      response.put("message", "창고가 성공적으로 등록되었습니다.");
      response.put("warehouseId", warehouse.getId());
      response.put("warehouseName", warehouse.getTitle());

      log.info("창고 등록 성공 - ID: {}, 창고명: {}", warehouse.getId(), warehouse.getTitle());

      return ResponseEntity.ok(response);

    } catch (IllegalArgumentException e) {
      log.error("창고 등록 실패 - 유효성 오류: {}", e.getMessage());
      Map<String, Object> errorResponse = new HashMap<>();
      errorResponse.put("success", false);
      errorResponse.put("message", e.getMessage());
      return ResponseEntity.badRequest().body(errorResponse);

    } catch (Exception e) {
      log.error("창고 등록 실패 - 서버 오류", e);
      Map<String, Object> errorResponse = new HashMap<>();
      errorResponse.put("success", false);
      errorResponse.put("message", "서버 오류가 발생했습니다. 잠시 후 다시 시도해주세요.");
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
  }

  @GetMapping("/list")
  public ResponseEntity<List<WarehouseDTO>> getAllWarehouses() {
    try {
      log.info("창고 목록 조회 API 호출");
      List<WarehouseDTO> warehouses = warehouseService.getAllWarehouses();
      log.info("창고 목록 조회 완료 - 총 {}개", warehouses.size());
      return ResponseEntity.ok(warehouses);
    } catch (Exception e) {
      log.error("창고 목록 조회 실패", e);
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
  }
}