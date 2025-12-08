package com.semo.dto.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TenantProfileDTO {

  private Long memberId;
  private String address;
  private String detailedAddress;
  private String purpose;
  private String requiredSizeCode;

}
