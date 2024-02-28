package com.samcomo.dbz.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

  // Global

  INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버에 문제가 발생했습니다."),

  INVALID_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),

  ACCESS_DENIED(HttpStatus.FORBIDDEN, "접근이 거부되었습니다."),

  // AWS S3
  AWS_SDK_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "AWS S3 SDK 에러가 발생하여 정보를 처리할 수 없습니다."),

  // Report
  IMAGE_UPLOAD_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 저장중 문제가 발생하였습니다."),

  // Member
  MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "유저 정보를 찾을 수 없습니다."),

  // Report
  REPORT_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글 정보를 찾을 수 없습니다."),
  NOT_SAME_MEMBER(HttpStatus.BAD_REQUEST, "작성자와 유저의 정보가 일치하지 않습니다.");

  private final HttpStatus status;
  private final String message;
}
