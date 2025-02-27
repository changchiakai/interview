package com.careline.interview.test.mission10;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/mission10")
public class Mission10Controller {

    private int callTime = 0;


    // 題目有點問題
    private static int totalCallCount = 0; // 總呼叫次數
    private static ConcurrentHashMap<String, Integer> sessionCallCounts = new ConcurrentHashMap<>(); // 存儲每個 sessionId 的呼叫次數

    @PostMapping("/call")
    public String call(HttpSession session) {
        String sessionId = session.getId();
        System.out.println("sessionId:"+ sessionId);
        // 根據 sessionId 獲取當前的呼叫次數
        sessionCallCounts.putIfAbsent(sessionId, 0); // 如果該 sessionId 尚未存在，則初始化為 0
        int currentCount = sessionCallCounts.get(sessionId);

        // 更新呼叫次數
        currentCount++;
        sessionCallCounts.put(sessionId, currentCount);

        // 更新總呼叫次數
        totalCallCount++;

        return String.format("{\"totalCallCount\": %d, \"sessionId\": \"%s\", \"sessionCount\": %d}",
                totalCallCount, sessionId, currentCount);

    }

    @PostMapping("/resetSession")
    public String resetSession(HttpSession session) {
        // 使當前 session 失效
        session.invalidate();

        // 返回新的 sessionId
        String newSessionId = session.getId();
        return String.format("Session has been reset. New sessionId: %s", newSessionId);
    }
}
