package com.semo.service.impl;

import com.semo.dto.member.WarehouseDTO;
import com.semo.dto.member.WarehouseRegisterDTO;
import com.semo.entity.Member;
import com.semo.entity.Warehouse;
import com.semo.repository.MemberRepository;
import com.semo.repository.WarehouseRepository;
import com.semo.service.WarehouseService;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class WarehouseServiceImpl implements WarehouseService {

  private final WarehouseRepository warehouseRepository;
  private final MemberRepository memberRepository;

  @Value("${com.semo.file.upload-dir}")
  private String uploadDir;

  @Override
  @Transactional
  public Warehouse registerWarehouse(WarehouseRegisterDTO dto, Long ownerId, MultipartFile image) {
    log.info("창고 등록 시작 - 임대인 ID: {}, 창고명: {}", ownerId, dto.getTitle());

    // 임대인(Owner) 조회
    Member owner = memberRepository.findById(ownerId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다. ID: " + ownerId));

    // 이미지 파일 저장
    String imagePath = null;
    if (image != null && !image.isEmpty()) {
      try {
        imagePath = saveImage(image);
        log.info("이미지 저장 완료: {}", imagePath);
      } catch (IOException e) {
        log.error("이미지 저장 실패", e);
        throw new RuntimeException("이미지 저장에 실패했습니다.", e);
      }
    }

    // Warehouse Entity 생성
    Warehouse warehouse = Warehouse.builder()
        .owner(owner)
        .title(dto.getTitle())
        .description(dto.getDescription())
        .address(dto.getAddress())
        .areaSqm(dto.getAreaSqm())
        .pricePerMonth(dto.getPricePerMonth())
        .availableStatus(dto.getAvailableStatus())
        .latitude(dto.getLatitude())
        .longitude(dto.getLongitude())
        .imagePath(imagePath)
        .build();

    // 저장
    Warehouse savedWarehouse = warehouseRepository.save(warehouse);

    log.info("창고 등록 완료 - 창고 ID: {}, 창고명: {}", savedWarehouse.getId(), savedWarehouse.getTitle());

    return savedWarehouse;
  }

  private String saveImage(MultipartFile file) throws IOException {
    // 업로드 디렉토리 생성
    Path uploadPath = Paths.get(uploadDir, "warehouses");
    if (!Files.exists(uploadPath)) {
      Files.createDirectories(uploadPath);
    }

    // 파일명 생성 (UUID + 원본 확장자)
    String originalFilename = file.getOriginalFilename();
    String extension = originalFilename != null && originalFilename.contains(".")
        ? originalFilename.substring(originalFilename.lastIndexOf("."))
        : "";
    String savedFilename = UUID.randomUUID().toString() + extension;

    // 파일 저장
    Path filePath = uploadPath.resolve(savedFilename);
    Files.copy(file.getInputStream(), filePath);

    // 저장된 경로 반환 (상대 경로)
    return "/uploads/warehouses/" + savedFilename;
  }

  @Override
  public List<WarehouseDTO> getAllWarehouses() {
    log.info("전체 창고 목록 조회");

    List<Warehouse> warehouses = warehouseRepository.findAll();

    return warehouses.stream()
        .map(this::convertToDTO)
        .collect(Collectors.toList());
  }

  @Override
  public List<WarehouseDTO> getWarehousesByOwnerId(Long ownerId) {
    log.info("소유주 ID {}의 창고 목록 조회", ownerId);

    List<Warehouse> warehouses = warehouseRepository.findByOwnerId(ownerId);

    return warehouses.stream()
        .map(this::convertToDTO)
        .collect(Collectors.toList());
  }

  @Override
  public WarehouseDTO getWarehouseById(Long warehouseId) {
    log.info("창고 ID {} 상세 조회", warehouseId);

    Warehouse warehouse = warehouseRepository.findById(warehouseId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 창고입니다. ID: " + warehouseId));

    return convertToDTO(warehouse);
  }

  @Override
  @Transactional
  public WarehouseDTO updateWarehouse(Long warehouseId, WarehouseRegisterDTO dto, MultipartFile image) {
    log.info("창고 ID {} 수정 시작", warehouseId);

    // 기존 창고 조회
    Warehouse warehouse = warehouseRepository.findById(warehouseId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 창고입니다. ID: " + warehouseId));

    // 이미지 업데이트 (새 이미지가 있는 경우)
    if (image != null && !image.isEmpty()) {
      try {
        String newImagePath = saveImage(image);
        warehouse.updateImagePath(newImagePath);
        log.info("이미지 수정 완료: {}", newImagePath);
      } catch (IOException e) {
        log.error("이미지 저장 실패", e);
        throw new RuntimeException("이미지 저장에 실패했습니다.", e);
      }
    }

    // 창고 정보 업데이트
    warehouse.updateWarehouse(
        dto.getTitle(),
        dto.getDescription(),
        dto.getAddress(),
        dto.getAreaSqm(),
        dto.getPricePerMonth(),
        dto.getAvailableStatus(),
        dto.getLatitude(),
        dto.getLongitude()
    );

    Warehouse updatedWarehouse = warehouseRepository.save(warehouse);

    log.info("창고 수정 완료 - 창고 ID: {}, 창고명: {}", updatedWarehouse.getId(), updatedWarehouse.getTitle());

    return convertToDTO(updatedWarehouse);
  }

  @Override
  @Transactional
  public void deleteWarehouse(Long warehouseId, Long ownerId) {
    log.info("창고 ID {} 삭제 시작 (소유주 ID: {})", warehouseId, ownerId);

    // 창고 조회
    Warehouse warehouse = warehouseRepository.findById(warehouseId)
        .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 창고입니다. ID: " + warehouseId));

    // 소유주 권한 확인
    if (!warehouse.getOwner().getId().equals(ownerId)) {
      throw new IllegalStateException("창고를 삭제할 권한이 없습니다.");
    }

    // 삭제
    warehouseRepository.delete(warehouse);

    log.info("창고 삭제 완료 - 창고 ID: {}", warehouseId);
  }

  private WarehouseDTO convertToDTO(Warehouse warehouse) {
    return WarehouseDTO.builder()
        .id(warehouse.getId())
        .title(warehouse.getTitle())
        .description(warehouse.getDescription())
        .address(warehouse.getAddress())
        .areaSqm(warehouse.getAreaSqm())
        .pricePerMonth(warehouse.getPricePerMonth())
        .availableStatus(warehouse.getAvailableStatus())
        .latitude(warehouse.getLatitude())
        .longitude(warehouse.getLongitude())
        .ownerId(warehouse.getOwner().getId())
        .ownerName(warehouse.getOwner().getName())
        .imagePath(warehouse.getImagePath())
        .build();
  }
}