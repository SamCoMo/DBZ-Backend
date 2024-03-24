package com.samcomo.dbz.member.model.dto;

import lombok.Builder;
import lombok.Setter;

@Setter
@Builder
public class TokenValidationResponse {
  private Boolean isValid;
  private Boolean isExpired;
}
