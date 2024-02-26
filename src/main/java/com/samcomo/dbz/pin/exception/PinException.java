package com.samcomo.dbz.pin.exception;


import com.samcomo.dbz.global.exception.CustomException;
import com.samcomo.dbz.global.exception.ErrorCode;

public class PinException extends CustomException {

  public PinException(ErrorCode errorCode) {
    super(errorCode);
  }
}
