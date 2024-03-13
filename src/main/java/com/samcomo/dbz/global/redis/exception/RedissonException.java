package com.samcomo.dbz.global.redis.exception;

import com.samcomo.dbz.global.exception.CustomException;
import com.samcomo.dbz.global.exception.ErrorCode;

public class RedissonException extends CustomException {

  public RedissonException(ErrorCode errorCode) {
    super(errorCode);
  }
}
