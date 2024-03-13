package com.samcomo.dbz.global.redis.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class AopForTransaction {

  @Transactional
  public Object proceed(ProceedingJoinPoint joinPoint) throws Throwable {
    return joinPoint.proceed();
  }

}
