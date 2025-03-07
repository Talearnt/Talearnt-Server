package com.talearnt.util.log;

import lombok.extern.log4j.Log4j2;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Log4j2
public class RunningTimeLogger {

    @Around("@annotation(LogRunningTime)")
    public Object logRunningTime(ProceedingJoinPoint joinPoint)throws Throwable{
        long startTime = System.currentTimeMillis();

        //실제 메서드 실행
        Object result = joinPoint.proceed();

        long duration = System.currentTimeMillis() - startTime;

        log.info("실행 시간 : {} ms, 위치 : {}", duration, joinPoint.getSignature());

        return result;
    }
}
