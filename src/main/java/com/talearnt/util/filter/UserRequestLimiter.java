package com.talearnt.util.filter;

import org.springframework.stereotype.Component;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class UserRequestLimiter {
    // userId -> 요청 시간 목록
    private final Map<Long, Deque<Long>> userRequests = new ConcurrentHashMap<>();

    // 제한 값
    private final int MAX_REQUESTS = 10;
    private final long TIME_WINDOW = 60 * 1000L; // 1분

    public synchronized boolean isAllowed(Long userId) {
        long now = System.currentTimeMillis();
        userRequests.putIfAbsent(userId, new ArrayDeque<>());

        Deque<Long> requests = userRequests.get(userId);

        // 1분이 지난 요청은 제거
        while (!requests.isEmpty() && now - requests.peekFirst() > TIME_WINDOW) {
            requests.pollFirst();
        }

        if (requests.size() >= MAX_REQUESTS) {
            return false; // 요청 초과
        }

        requests.addLast(now);
        return true; // 허용
    }
}
