package com.careline.interview.test.mission10;

import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/mission10")
public class Mission10Controller {
    private static ConcurrentHashMap<String, Integer> sessionCallCounts = new ConcurrentHashMap<>(); // 存儲每個 sessionId 的呼叫次數

    @PostMapping("/call")
    public int call(@RequestHeader("X-Session-Id") String sessionId) {
        // 根據 sessionId 獲取當前的呼叫次數
        sessionCallCounts.putIfAbsent(sessionId, 0); // 如果該 sessionId 尚未存在，則初始化為 0
        int currentCount = sessionCallCounts.get(sessionId);

        // 更新呼叫次數
        currentCount++;
        sessionCallCounts.put(sessionId, currentCount);


        return currentCount;

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
