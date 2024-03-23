package com.samcomo.dbz.notification.model.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SendSingleDto {

  private String title;
  private String body;
  private String token;

}
