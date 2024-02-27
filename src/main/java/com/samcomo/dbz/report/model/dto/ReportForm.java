package com.samcomo.dbz.report.model.dto;

import com.samcomo.dbz.report.model.constants.PetType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class ReportForm {

  private String title;
  private PetType petType;
  private boolean showsPhone;
  private String species;
  private Integer age;
  private String descriptions;
  private String petName;
  private String feature;
  private String streetAddress;
  private String roadAddress;
  private double latitude;
  private double longitude;

}
