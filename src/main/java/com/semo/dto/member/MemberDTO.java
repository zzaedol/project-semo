package com.semo.dto.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberDTO {

  private Long id;
  private String email;
  private String password;
  private String name;
  private String phoneNumber;
}
