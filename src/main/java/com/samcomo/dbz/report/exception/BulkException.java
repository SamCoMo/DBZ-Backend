package com.samcomo.dbz.report.exception;

import com.samcomo.dbz.global.exception.CustomException;
import com.samcomo.dbz.global.exception.ErrorCode;

public class BulkException extends CustomException {

  public BulkException(ErrorCode errorCode) {
    super(errorCode);
  }
}
