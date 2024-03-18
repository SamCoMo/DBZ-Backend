package com.samcomo.dbz.report.model.dto;

import static lombok.AccessLevel.PROTECTED;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor(access = PROTECTED)
@AllArgsConstructor(access = PROTECTED)
public class CustomSlice<T> {

  private List<T> content;
  private CustomPageable pageable;
  private boolean first;
  private boolean last;
  private Integer number;
  private Integer size;
  private Integer numberOfElements;
}
