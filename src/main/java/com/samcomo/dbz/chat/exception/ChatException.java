package com.samcomo.dbz.chat.exception;


import com.samcomo.dbz.global.exception.CustomException;
import com.samcomo.dbz.global.exception.ErrorCode;

public class ChatException extends CustomException {

  public ChatException(ErrorCode errorCode) {
    super(errorCode);
  }
}
