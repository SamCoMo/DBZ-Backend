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

  INVALID_BASE64_DATA(HttpStatus.INTERNAL_SERVER_ERROR, "Base64 데이터가 올바르지 않습니다."),

  INVALID_IMAGE_FILE_TYPE(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 파일형식이 올바르지 않습니다."),

  IMAGE_FILE_SIZE_EXCEEDED(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 파일 크기 제한을 초과하였습니다."),


  // Member

  MEMBER_NOT_FOUND(HttpStatus.NOT_FOUND, "유저 정보를 찾을 수 없습니다."),

  EMAIL_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 존재하는 이메일 계정입니다."),

  NICKNAME_ALREADY_EXISTS(HttpStatus.BAD_REQUEST, "이미 존재하는 닉네임입니다."),

  INVALID_SESSION(HttpStatus.UNAUTHORIZED, "세션 정보가 유효하지 않습니다."),

  // Report

  IMAGE_UPLOAD_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "이미지 저장중 문제가 발생하였습니다."),

  REPORT_NOT_FOUND(HttpStatus.NOT_FOUND, "게시글 정보를 찾을 수 없습니다."),

  NOT_SAME_MEMBER(HttpStatus.BAD_REQUEST, "작성자와 멤버의 정보가 일치하지 않습니다."),

  // Redis-Lock

  LOCK_FAIL(HttpStatus.INTERNAL_SERVER_ERROR, "Lock 획득에 실패하였습니다."),

  // Chat

  ACCESS_DENIED_CHATROOM(HttpStatus.FORBIDDEN, "채팅방에 접근이 거부되었습니다."),

  CHATROOM_NOT_FOUND(HttpStatus.BAD_REQUEST, "채팅방을 찾을 수 없습니다."),

  CHAT_MESSAGE_NOT_FOUND(HttpStatus.BAD_REQUEST, "채팅을 찾을 수 없습니다."),

  // Notification

  PIN_NOTIFICATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "핀 알림 전송을 실패했습니다."),
  REPORT_NOTIFICATION_FAILED(HttpStatus.INTERNAL_SERVER_ERROR, "게시글 알림 전송을 실패했습니다."),
  ACCESS_TOKEN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Access Token 획득에 실패했습니다.");

  private final HttpStatus status;
  private final String message;
}
