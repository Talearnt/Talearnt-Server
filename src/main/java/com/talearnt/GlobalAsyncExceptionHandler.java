package com.talearnt;

import lombok.extern.log4j.Log4j2;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;


@Log4j2
public class GlobalAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

    @Override
    public void handleUncaughtException(Throwable ex, Method method, Object... params) {
        // 로그에 에러 발생 시점 포함
        log.error("\n\n====================== [@Async 예외 발생] ======================");
        log.error("⏰ 시간: {}", LocalDateTime.now());
        log.error("📍 메서드: {}.{}", method.getDeclaringClass().getName(), method.getName());
        log.error("🧾 파라미터: {}", Arrays.toString(params));
        log.error("🧨 예외 클래스: {}", ex.getClass().getName());
        log.error("💬 메시지: {}", ex.getMessage());

        // 전체 스택 트레이스 로그 출력
        Arrays.stream(ex.getStackTrace())
                .limit(10) // 최대 10줄까지만 표시 (과하면 생략)
                .forEach(stackTrace -> log.error("    at {}", stackTrace.toString()));

        if (ex.getCause() != null) {
            log.error("🔁 Root Cause: {} - {}", ex.getCause().getClass().getName(), ex.getCause().getMessage());
        }

        log.error("===========================================================\n");
    }
}
