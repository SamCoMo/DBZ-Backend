package com.samcomo.dbz.global.s3.constants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ImageFileType {
  JPG("image/jpeg", "jpg"),
  JPEG("image/jpeg", "jpeg"),
  PNG("image/png", "png");

  private final String mimeType;
  private final String extension;
  private static final Set<String> VALID_IMAGE_EXTENSTION
      = new HashSet<>(Arrays.asList("jpg","jpeg","png"));

  public static boolean isValidImageFileType(String extension){
    return VALID_IMAGE_EXTENSTION.contains(extension);

  }
}
