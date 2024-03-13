package com.samcomo.dbz.report.model.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CustomSlice<T> {

  private List<T> content;
  private CustomPageable pageable;
  private boolean first;
  private boolean last;
  private int number;
  private int size;
  private int numberOfElements;

}
