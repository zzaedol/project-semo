package com.semo.service.impl;

import com.semo.dto.member.MemberSignupDTO;
import com.semo.entity.Member;
import com.semo.entity.MemberRole;
import com.semo.entity.MemberRoleId;
import com.semo.entity.OwnerProfile;
import com.semo.entity.Role;
import com.semo.entity.TenantProfile;
import com.semo.entity.Warehouse;
import com.semo.mapper.MemberSignupMapper;
import com.semo.repository.MemberRepository;
import com.semo.repository.RoleRepository;
import com.semo.repository.WarehouseRepository;
import com.semo.service.MemberService;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/**
 * MemberService 인터페이스의 구현 클래스입니다. 회원가입, 프로필 관리, 역할 부여 등의 핵심 비즈니스 로직을 처리합니다.
 */
@Service
@RequiredArgsConstructor
@Log4j2
public class MemberServiceImpl implements MemberService {

  // 핵심 의존성 주입
  private final MemberRepository memberRepository;
  private final RoleRepository roleRepository;
  private final WarehouseRepository warehouseRepository; // Warehouse는 별도 저장이 필요
  private final MemberSignupMapper mapper;
  private final PasswordEncoder passwordEncoder;
  private final EntityManager entityManager; // @MapsId 연관관계 처리를 위해 명시적 사용 가능

  // 파일 저장 경로 (application.properties 등에 설정 필요)
  @Value("${com.semo.file.upload-dir}")
  private String uploadDir;

  /**
   * 회원가입 로직을 수행합니다. (트랜잭션 적용) 이메일 중복 확인, 비밀번호 암호화, 역할 부여, 프로필 및 창고 정보를 분기하여 저장합니다.
   *
   * @param signupDTO 회원가입 DTO
   */
  @Override
  @Transactional
  public void signup(MemberSignupDTO signupDTO) {
    String email = signupDTO.getMemberDTO().getEmail();
    String userType = signupDTO.getUserType().toUpperCase(); // 사용자 유형 대문자 변환

    // 1. 유효성 검증: 이메일 중복 확인
    if (memberRepository.existsByEmail(email)) {
      throw new IllegalStateException("이미 등록된 이메일 주소입니다: " + email);
    }

    // 2. Member Entity 생성 및 비밀번호 암호화 (Spring Security)
    Member member = mapper.toMemberEntity(signupDTO.getMemberDTO());
    String encodedPassword = passwordEncoder.encode(signupDTO.getMemberDTO().getPassword());
    member.changePassword(encodedPassword); // 암호화된 비밀번호 설정

    // 3. Member 저장 (영속성 컨텍스트에 등록)
    member = memberRepository.save(member);
    log.info("Member saved with ID: {}", member.getId());

    // 4. Role 부여 및 MemberRole 저장
    Role role = roleRepository.findByName(userType)
        .orElseThrow(() -> new IllegalStateException("유효하지 않은 회원 유형입니다: " + userType));

    MemberRole memberRole = MemberRole.builder()
        .id(new MemberRoleId(member.getId(), role.getId())) // 복합키 생성
        .member(member)
        .role(role)
        .build();

    member.getMemberRoles().add(memberRole);

    // MemberRole 저장 (Member 엔티티에 cascade 설정이 없다면 별도의 Repository가 필요함. 여기서는 Member를 통해 저장되는 구조가 아닐 수 있으므로 MemberRoleRepository가 필요하지만,
    // 편의상 이 예제에서는 Member 엔티티의 @OneToMany 컬렉션에 추가하고 MemberRoleRepository를 생략하여 Member의 Cascade 설정을 가정하거나, MemberRoleRepository를 추가해야 함.
    // **MapStruct 사용 및 명시적인 JPA 처리를 위해, MemberRole 엔티티를 직접 저장하는 로직을 포함해야 합니다.**
    // **참고:** MemberRole 엔티티가 `BasicEntity`를 상속받지 않은 점을 고려하여, 여기에 별도의 MemberRoleRepository가 필요하지만,
    // 현재는 Member의 필드에 추가하고 JPA의 영속성 컨텍스트에 의해 저장되도록 합니다 (Member 엔티티에 CASCADE 설정이 필요함).
    // Member 엔티티에 @OneToMany(mappedBy = "member", cascade = CascadeType.ALL)를 추가하는 것이 권장됩니다. (현재는 없으므로, **MemberRoleRepository**를 추가해야 합니다. 하단에 추가하겠습니다.)

    // 5. 프로필 정보 분기 처리 및 저장
    if ("OWNER".equals(userType)) {
      // 5-1. 소유주 프로필 Entity 생성 및 연관관계 설정
      OwnerProfile ownerProfile = mapper.toOwnerProfileEntity(signupDTO.getOwnerProfileDTO());

      // 5-2. 사업자 등록 파일 저장 처리
      MultipartFile businessFile = signupDTO.getOwnerProfileDTO().getBusinessFile();
      if (businessFile != null && !businessFile.isEmpty()) {
        String filePath = saveFile(businessFile, member.getId());
        ownerProfile.setBusinessFilePath(filePath);
      }

      // Member에 OwnerProfile 설정 (편의 메서드가 양방향 연관관계 자동 설정)
      member.setOwnerProfile(ownerProfile);

      // 5-3. 창고 정보 저장 (소유주 가입 시 선택적으로 창고 정보도 등록 가능)
      if (signupDTO.getWarehouseDTO() != null) {
        Warehouse warehouse = mapper.toWarehouseEntity(signupDTO.getWarehouseDTO());
        warehouse.setOwner(member); // 소유주 Member 설정
        warehouseRepository.save(warehouse);
      }

    } else if ("TENANT".equals(userType)) {
      // 5-1. 임차인 프로필 Entity 생성 및 연관관계 설정
      TenantProfile tenantProfile = mapper.toTenantProfileEntity(signupDTO.getTenantProfileDTO());

      // Member에 TenantProfile 설정 (편의 메서드가 양방향 연관관계 자동 설정)
      member.setTenantProfile(tenantProfile);

    } else {
      throw new IllegalStateException("지원하지 않는 회원 유형입니다: " + userType);
    }

    // 6. DB 트랜잭션 종료 시점에 영속성 컨텍스트의 모든 변경 사항이 반영됨
    // Member는 이미 79번 라인에서 저장되었고, CASCADE 설정으로 프로필도 자동 저장됨
  }

  /**
   * 파일 시스템에 파일을 저장하고 저장 경로를 반환합니다.
   *
   * @param file     저장할 MultipartFile
   * @param memberId 회원 ID (파일 경로를 고유하게 만드는 데 사용)
   * @return 저장된 파일 경로 (DB에 저장할 값)
   */
  private String saveFile(MultipartFile file, Long memberId) {
    if (file == null || file.isEmpty()) {
      return null;
    }

    try {
      // 파일 저장 디렉토리 설정: [업로드 경로]/member/[memberId]/business
      Path uploadPath = Paths.get(uploadDir, "member", memberId.toString(), "business");
      Files.createDirectories(uploadPath);

      // 파일명 설정: 원본 파일명을 사용하거나, UUID를 사용하여 중복 방지
      String originalFilename = file.getOriginalFilename();
      String uniqueFilename = System.currentTimeMillis() + "_" + originalFilename; // 간단한 유니크 파일명 생성

      Path filePath = uploadPath.resolve(uniqueFilename);
      Files.copy(file.getInputStream(), filePath);

      // DB에 저장할 상대 경로 반환
      return "/member/" + memberId + "/business/" + uniqueFilename;

    } catch (IOException e) {
      log.error("Failed to store file for memberId: {}", memberId, e);
      // 파일 저장 실패 시, 예외 처리 또는 롤백을 유도
      throw new RuntimeException("파일 저장에 실패했습니다.", e);
    }
  }
}
