package com.samcomo.dbz.global.s3.constants;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Builder
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ImageUploadState {

  private boolean success;
  private String imageUrl;
}
