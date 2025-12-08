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
@Table(name = "tenant_profile")
public class TenantProfile extends BasicEntity {

  @Id
  @Column(name = "member_id", nullable = false)
  private Long id;

  @MapsId
  @OneToOne(fetch = FetchType.LAZY, optional = false)
  @OnDelete(action = OnDeleteAction.CASCADE)
  @JoinColumn(name = "member_id", nullable = false)
  private Member member;

  @Column(name = "address", length = 500)
  private String address;

  @Column(name = "detailed_address")
  private String detailedAddress;

  @Column(name = "purpose", length = 50)
  private String purpose;

  @Column(name = "required_size_code", length = 10)
  private String requiredSizeCode;

  public void setId(Long id) {
    this.id = id;
  }

  public void setMember(Member member) {
    this.member = member;
  }

}