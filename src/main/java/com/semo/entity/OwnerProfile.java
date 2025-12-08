package com.semo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "owner_profile")
public class OwnerProfile extends BasicEntity {

  @Id
  @Column(name = "member_id", nullable = false)
  private Long id;

  @MapsId
  @OneToOne(fetch = FetchType.LAZY, optional = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  @Column(name = "business_registration_number", nullable = false, length = 20)
  private String businessRegistrationNumber;

  @Column(name = "company_name")
  private String companyName;

  @Column(name = "ceo_name", nullable = false, length = 100)
  private String ceoName;

  @Column(name = "office_phone_number", length = 20)
  private String officePhoneNumber;

  @Column(name = "business_file_path", length = 500)
  private String businessFilePath;

  public void setId(Long id) {
    this.id = id;
  }

  public void setBusinessFilePath(String businessFilePath) {
    this.businessFilePath = businessFilePath;
  }

  public void setMember(Member member) {
    this.member = member;
  }

}