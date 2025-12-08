package com.semo.dto.member;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseRegisterDTO {

  @NotBlank(message = "창고명은 필수입니다.")
  private String title;

  private String description;

  @NotBlank(message = "주소는 필수입니다.")
  private String address;

  @NotNull(message = "면적은 필수입니다.")
  @DecimalMin(value = "0.01", message = "면적은 0보다 커야 합니다.")
  private BigDecimal areaSqm;

  @NotNull(message = "월 임대료는 필수입니다.")
  @DecimalMin(value = "0", message = "월 임대료는 0 이상이어야 합니다.")
  private BigDecimal pricePerMonth;

  @Builder.Default
  private Boolean availableStatus = false;

  // 위치 정보 (선택 사항)
  private Double latitude;
  private Double longitude;

  // 이미지 경로 (업로드 후 저장)
  private String imagePath;
}