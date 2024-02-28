package com.samcomo.dbz.global.s3.exception;

import com.samcomo.dbz.global.exception.CustomException;
import com.samcomo.dbz.global.exception.ErrorCode;

public class S3Exception extends CustomException {

  public S3Exception(ErrorCode errorCode) {
    super(errorCode);
  }
}
