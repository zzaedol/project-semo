package com.semo.dto.member;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OwnerProfileDTO implements Serializable {

  private Long memberId;
  private String businessRegistrationNumber;
  private String companyName;
  private String ceoName;
  private String officePhoneNumber;
  private MultipartFile businessFile;
  private String businessFilePath;
}
