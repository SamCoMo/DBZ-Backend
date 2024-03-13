package com.samcomo.dbz.report.model.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CustomPageable {

  private int pageNumber;
  private int pageSize;

}
