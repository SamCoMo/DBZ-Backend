package com.samcomo.dbz.global.redis.aop;

import com.samcomo.dbz.global.exception.ErrorCode;
import com.samcomo.dbz.global.redis.LockType;
import com.samcomo.dbz.global.redis.exception.RedissonException;
import java.lang.reflect.Method;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class DistributedLockAop {

  private final RedissonClient redissonClient;
  private final AopForTransaction aopForTransaction;

  @Around("@annotation(com.samcomo.dbz.global.redis.aop.DistributedLock)")
  public Object lock(ProceedingJoinPoint joinPoint) throws Throwable{

    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();
    DistributedLock distributedLock = method.getAnnotation(DistributedLock.class);

    String lockName = distributedLock.key();
    LockType type = distributedLock.lockType();
    RLock lock = redissonClient.getLock(type + ":" + lockName);

    try{
      if (!lock.tryLock(
          distributedLock.waitTime(), distributedLock.leaseTime(), distributedLock.timeUnit())){
        throw new RedissonException(ErrorCode.LOCK_FAIL);
      }

      log.info("Lock 획득!");

      return aopForTransaction.proceed(joinPoint);

    }catch (InterruptedException e){
      throw new RedissonException(ErrorCode.LOCK_FAIL);
    }finally {
      try{
        lock.unlock();
        log.info("Lock 제거");
      }catch (IllegalMonitorStateException e) {
        log.info("이미 Lock을 제거했습니다.");
      }
    }
  }

}
