package com.samcomo.dbz.global.redis.aop;

import com.samcomo.dbz.global.redis.LockType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DistributedLock {

  String key();

  LockType lockType();

  TimeUnit timeUnit() default TimeUnit.SECONDS;

  // Lock 획득을 위해 기다리는 시간
  long waitTime() default 1L;

  // Lock을 소유하고 있는 시간
  long leaseTime() default 3L;

}
