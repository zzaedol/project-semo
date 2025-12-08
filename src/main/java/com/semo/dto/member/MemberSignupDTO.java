package com.semo.dto.member;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberSignupDTO implements Serializable {

  private String userType;

  private MemberDTO memberDTO;

  private TenantProfileDTO tenantProfileDTO;
  private OwnerProfileDTO ownerProfileDTO;
  private WarehouseDTO warehouseDTO;

  private TermsDTO termsDTO;

}
