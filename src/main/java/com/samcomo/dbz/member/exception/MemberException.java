package com.samcomo.dbz.member.exception;

import com.samcomo.dbz.global.exception.CustomException;
import com.samcomo.dbz.global.exception.ErrorCode;

public class MemberException extends CustomException {
  public MemberException(ErrorCode errorCode) {
    super(errorCode);
  }
}
