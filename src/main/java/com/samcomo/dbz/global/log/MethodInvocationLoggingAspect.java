package com.samcomo.dbz.global.log;

import com.samcomo.dbz.global.exception.ErrorCode;
import java.util.Arrays;
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
public class MethodInvocationLoggingAspect {
  @Around("@annotation(LogMethodInvocation)")
  public Object logMethod(ProceedingJoinPoint joinPoint) throws Throwable {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();

//    ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
//    HttpServletRequest request = attributes.getRequest();
//    String requestId = (String) request.getAttribute("RequestID");

    log.info("[{}], Parameter: {}", signature.getMethod().getName(),  Arrays.toString(joinPoint.getArgs()));

    Object result = ErrorCode.INTERNAL_SERVER_ERROR; // 문제가 생기면 ERROR 반환
    try {
      result = joinPoint.proceed();
    } finally {
      log.info("[{}], Result: {}", signature.getMethod().getName() , result);
    }

    return result;
  }
}
