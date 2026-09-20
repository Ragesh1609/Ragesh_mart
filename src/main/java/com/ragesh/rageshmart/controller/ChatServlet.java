package com.ragesh.rageshmart.controller;

import com.ragesh.rageshmart.chat.ChatProvider;
import com.ragesh.rageshmart.chat.ChatProviderFactory;
import com.ragesh.rageshmart.dto.ApiResponse;
import com.ragesh.rageshmart.util.JsonUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Section 11/17: chatbot backend proxy. Validates input, enforces a 10 msg/min
 * per-session rate limit, caps input length, and caches repeated identical
 * questions per session. The API key never reaches the client - only this
 * servlet talks to ChatProvider.
 */
@WebServlet("/api/chat")
public class ChatServlet extends HttpServlet {

    private static final int MAX_MESSAGES_PER_MINUTE = 10;
    private static final int MAX_MESSAGE_LENGTH = 500;
    private static final String RATE_LIMIT_ATTR = "chatRateLimitWindow";
    private static final String CACHE_ATTR = "chatResponseCache";

    private final ChatProvider chatProvider = ChatProviderFactory.create();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(true);

        if (isRateLimited(session)) {
            // 429 Too Many Requests - javax.servlet-api 4.0.1 (Tomcat 9) has no SC_TOO_MANY_REQUESTS
            // constant (that was only added in the later Jakarta namespace), so use the literal code.
            JsonUtil.writeJson(resp, 429,
                    ApiResponse.fail("RATE_LIMITED", "Please wait a moment before sending more messages"));
            return;
        }

        ChatRequest body = JsonUtil.readJson(req, ChatRequest.class);
        if (body == null || body.message == null || body.message.isBlank()) {
            JsonUtil.writeJson(resp, HttpServletResponse.SC_BAD_REQUEST,
                    ApiResponse.fail("VALIDATION_ERROR", "message is required"));
            return;
        }
        String message = body.message.trim();
        if (message.length() > MAX_MESSAGE_LENGTH) {
            message = message.substring(0, MAX_MESSAGE_LENGTH);
        }

        @SuppressWarnings("unchecked")
        Map<String, String> cache = (Map<String, String>) session.getAttribute(CACHE_ATTR);
        if (cache == null) {
            cache = new ConcurrentHashMap<>();
            session.setAttribute(CACHE_ATTR, cache);
        }

        String cacheKey = message.toLowerCase();
        String reply = cache.get(cacheKey);
        if (reply == null) {
            try {
                reply = chatProvider.getReply(message, "RageshMart product/order support");
            } catch (Exception e) {
                reply = "Sorry, the assistant is temporarily unavailable. Please try again shortly.";
            }
            cache.put(cacheKey, reply);
        }

        JsonUtil.writeJson(resp, HttpServletResponse.SC_OK, ApiResponse.ok(Map.of("reply", reply)));
    }

    @SuppressWarnings("unchecked")
    private boolean isRateLimited(HttpSession session) {
        Deque<Long> window = (Deque<Long>) session.getAttribute(RATE_LIMIT_ATTR);
        if (window == null) {
            window = new ArrayDeque<>();
            session.setAttribute(RATE_LIMIT_ATTR, window);
        }
        long now = System.currentTimeMillis();
        while (!window.isEmpty() && now - window.peekFirst() > 60_000) {
            window.pollFirst();
        }
        if (window.size() >= MAX_MESSAGES_PER_MINUTE) {
            return true;
        }
        window.addLast(now);
        return false;
    }

    private static class ChatRequest {
        String message;
    }
}
