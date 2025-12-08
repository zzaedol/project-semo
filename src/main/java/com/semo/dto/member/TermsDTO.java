package com.semo.dto.member;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TermsDTO {

  private Boolean agreeAll;
  private Boolean terms1;
  private Boolean terms2;
  private Boolean terms3;
  private Boolean terms4;
  private Boolean terms5;
}
