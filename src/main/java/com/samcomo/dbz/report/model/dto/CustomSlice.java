package com.samcomo.dbz.report.model.dto;

import static lombok.AccessLevel.PROTECTED;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@Getter
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PROTECTED)
public class CustomSlice<T> {

  private List<T> content;
  private CustomPageable pageable;
  private Boolean first;
  private Boolean last;
  private Integer number;
  private Integer size;
  private Integer numberOfElements;
}
