package com.semo.mapper;

import com.semo.dto.member.MemberDTO;
import com.semo.dto.member.OwnerProfileDTO;
import com.semo.dto.member.TenantProfileDTO;
import com.semo.dto.member.WarehouseDTO;
import com.semo.entity.Member;
import com.semo.entity.OwnerProfile;
import com.semo.entity.TenantProfile;
import com.semo.entity.Warehouse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * 회원가입 관련 DTO와 Entity 간의 변환을 담당하는 MapStruct Mapper입니다.
 * MapStruct의 componentModel을 "spring"으로 설정하여 Spring Bean으로 등록합니다.
 */
@Mapper(componentModel = "spring")
public interface MemberSignupMapper {

  /**
   * MemberDTO를 Member Entity로 변환합니다.
   * 비밀번호는 Service 계층에서 암호화되므로, 매핑을 제외하고 Service에서 직접 설정합니다.
   * @param memberDTO 변환할 Member DTO
   * @return 변환된 Member Entity
   */
  @Mapping(target = "id", ignore = true) // ID는 DB에서 자동 생성
  @Mapping(target = "password", ignore = true) // 비밀번호는 Service에서 암호화 후 설정
  @Mapping(target = "memberRoles", ignore = true) // MemberRole은 Service에서 별도 생성
  Member toMemberEntity(MemberDTO memberDTO);

  /**
   * TenantProfileDTO를 TenantProfile Entity로 변환합니다.
   * @param tenantProfileDTO 변환할 TenantProfile DTO
   * @return 변환된 TenantProfile Entity
   */
  @Mapping(target = "id", ignore = true) // ID는 Member Entity의 ID를 공유 (MapsId)
  @Mapping(target = "member", ignore = true) // 연관관계는 Service에서 설정
  TenantProfile toTenantProfileEntity(TenantProfileDTO tenantProfileDTO);

  /**
   * OwnerProfileDTO를 OwnerProfile Entity로 변환합니다.
   * businessFile(MultipartFile)은 Entity에 없으므로 매핑을 무시합니다.
   * @param ownerProfileDTO 변환할 OwnerProfile DTO
   * @return 변환된 OwnerProfile Entity
   */
  @Mapping(target = "id", ignore = true) // ID는 Member Entity의 ID를 공유 (MapsId)
  @Mapping(target = "member", ignore = true) // 연관관계는 Service에서 설정
  //@Mapping(target = "businessFile", ignore = true) // MultipartFile은 Entity 매핑 대상이 아님
  OwnerProfile toOwnerProfileEntity(OwnerProfileDTO ownerProfileDTO);

  /**
   * WarehouseDTO를 Warehouse Entity로 변환합니다.
   * @param warehouseDTO 변환할 Warehouse DTO
   * @return 변환된 Warehouse Entity
   */
  @Mapping(target = "id", ignore = true) // ID는 DB에서 자동 생성
  @Mapping(target = "owner", ignore = true) // 소유주(Member) 연관관계는 Service에서 설정
  Warehouse toWarehouseEntity(WarehouseDTO warehouseDTO);
}
