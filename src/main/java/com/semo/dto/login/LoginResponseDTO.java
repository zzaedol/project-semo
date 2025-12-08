package com.semo.dto.login;

import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponseDTO {

  private String token;
  private Long id;
  private String email;
  private String name;
  private Set<String> roles; // 사용자 권한 목록
}
