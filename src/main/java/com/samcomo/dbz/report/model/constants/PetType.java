package com.samcomo.dbz.report.model.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum PetType {

  DOG("강아지"),
  CAT("고양이"),
  OTHER("기타");

  private final String description;
}
