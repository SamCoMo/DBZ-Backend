package com.samcomo.dbz.global.log;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class ExecutionTimeLoggingAspect {
  @Around("@annotation(LogTimeInvocation)")
  public Object logMethod(ProceedingJoinPoint joinPoint) throws Throwable {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    // 메서드 이름
    String methodName = signature.getMethod().getName();
    // 메서드 실행 전 시간 기록
    long start = System.currentTimeMillis();

    Object result;
    try {
      result = joinPoint.proceed();
    } finally {
      // 실행 시간 계산
      long executionTime = System.currentTimeMillis() - start;
      // 메서드의 실행 시간 로깅
      log.info("[{}], Execution time: {} ms", methodName, executionTime);
    }

    return result;
  }
}
