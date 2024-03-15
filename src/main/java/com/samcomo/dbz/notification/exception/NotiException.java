package com.samcomo.dbz.notification.exception;

import com.samcomo.dbz.global.exception.CustomException;
import com.samcomo.dbz.global.exception.ErrorCode;

public class NotiException extends CustomException {

  public NotiException(ErrorCode errorCode){
    super(errorCode);
  }

}
