package com.talearnt.util.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class DelayAspect {
    @Before("execution(* com.talearnt.post..*(..)) " +
            "|| execution(* com.talearnt.user.talent..*(..))"+
            "|| execution(* com.talearnt.s3..*(..))"
    )
    public void delayBeforeMethod() throws InterruptedException {
        Thread.sleep(700); // 0.7초 지연
    }
}
