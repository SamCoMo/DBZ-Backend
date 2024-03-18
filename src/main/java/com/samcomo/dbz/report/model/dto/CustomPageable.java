package com.samcomo.dbz.report.model.dto;

import static lombok.AccessLevel.PROTECTED;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = PROTECTED)
public class CustomPageable {

  public CustomPageable(int pageNumber, int pageSize) {

    this.pageNumber = pageNumber;
    this.pageSize = pageSize;
  }

  private Integer pageNumber;
  private Integer pageSize;
}
