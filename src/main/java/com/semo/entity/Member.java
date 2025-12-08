package com.semo.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.LinkedHashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "member")
public class Member extends BasicEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "email", nullable = false)
  private String email;

  @Column(name = "password", nullable = false)
  private String password;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "phone_number")
  private String phoneNumber;

  // 소유주 프로필 연관관계 설정 (Tenant와 Owner 중 하나만 존재)
  @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
  private OwnerProfile ownerProfile;

  // 임차인 프로필 연관관계 설정
  @OneToOne(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
  private TenantProfile tenantProfile;

  // MemberRole 연관관계 설정
  @OneToMany(mappedBy = "member", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private Set<MemberRole> memberRoles = new LinkedHashSet<>();

  // 연관관계 편의 메서드 추가
  public void setOwnerProfile(OwnerProfile ownerProfile) {
    this.ownerProfile = ownerProfile;
    if (ownerProfile != null) {
      ownerProfile.setMember(this);
      ownerProfile.setId(this.id);
    }
  }

  public void setTenantProfile(TenantProfile tenantProfile) {
    this.tenantProfile = tenantProfile;
    if (tenantProfile != null) {
      tenantProfile.setMember(this);
      tenantProfile.setId(this.id);
    }
  }

  public void changePassword(String password) {
    this.password = password;
  }

}