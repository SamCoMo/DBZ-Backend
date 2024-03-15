package com.samcomo.dbz.global.exception;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.samcomo.dbz.global.exception.dto.ErrorResponse;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  // 커스텀 예외 처리
  @ExceptionHandler(CustomException.class)
  public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
    log.error("{}", e.getMessage());
    ErrorResponse errorResponse = new ErrorResponse(e.getErrorCode(), e.getMessage());
    return ResponseEntity.status(e.getErrorCode().getStatus()).body(errorResponse);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<String> handleException(Exception e) {
    log.error("{}", e.getMessage());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
  }

  // @Valid 유효성 검사 실패 시 예외 처리
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<Map<String, String>> handleValidException(
      MethodArgumentNotValidException e) {

    Map<String, String> errorResponse = new HashMap<>();

    String[] arguments = Objects.requireNonNull(e.getDetailMessageArguments())[1]
        .toString().split(", and ");

    for (String arg : arguments) {
      String[] errorString = arg.split(": ");
      errorResponse.put(errorString[0], errorString[1]);
    }

    return ResponseEntity.status(BAD_REQUEST).body(errorResponse);
  }
}

