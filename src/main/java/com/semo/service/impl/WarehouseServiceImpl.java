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