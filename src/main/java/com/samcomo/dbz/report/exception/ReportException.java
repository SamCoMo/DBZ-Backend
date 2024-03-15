package com.samcomo.dbz.report.exception;


import com.samcomo.dbz.global.exception.CustomException;
import com.samcomo.dbz.global.exception.ErrorCode;

public class ReportException extends CustomException {

  public ReportException(ErrorCode errorCode) {
    super(errorCode);
  }
}
