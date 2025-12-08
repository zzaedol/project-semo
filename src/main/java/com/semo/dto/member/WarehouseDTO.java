package com.semo.dto.member;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseDTO {

  private Long id;
  private String title;
  private String description;
  private String address;
  private BigDecimal areaSqm;
  private BigDecimal pricePerMonth;
  @Builder.Default
  private Boolean availableStatus = false;
  private Double latitude;
  private Double longitude;
  private Long ownerId;
  private String ownerName;
  private String imagePath;
}
