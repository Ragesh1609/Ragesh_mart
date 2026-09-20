package com.ragesh.rageshmart.chat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

/**
 * Real LLM-backed provider. Section 11, requirement 1: the API key is read only from
 * a server-side environment variable / properties file - it must never appear in any
 * client-side code (chat-widget.js never sees it, only ChatServlet does, indirectly
 * through this class).
 *
 * Swap the endpoint/model below for whichever provider you actually have credentials
 * for; the ChatProvider interface is what the rest of the app depends on.
 */
public class GeminiChatProvider implements ChatProvider {

    private static final Logger log = LoggerFactory.getLogger(GeminiChatProvider.class);
    private static final Duration TIMEOUT = Duration.ofSeconds(8);

    private final String apiKey;
    private final HttpClient httpClient;

    public GeminiChatProvider(String apiKey) {
        this.apiKey = apiKey;
        this.httpClient = HttpClient.newBuilder().connectTimeout(TIMEOUT).build();
    }

    @Override
    public String getReply(String userMessage, String context) {
        if (apiKey == null || apiKey.isBlank()) {
            log.warn("GEMINI_API_KEY not configured; falling back to mock responses");
            return new MockChatProvider().getReply(userMessage, context);
        }
        try {
            String prompt = "You are the RageshMart support assistant. Only answer product/listing/order " +
                    "questions for this marketplace. Context: " + context + "\nUser: " + userMessage;

            String body = "{\"contents\":[{\"parts\":[{\"text\":\"" + escapeJson(prompt) + "\"}]}]}";

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create("https://generativelanguage.googleapis.com/v1beta/models/" +
                            "gemini-1.5-flash:generateContent?key=" + apiKey))
                    .timeout(TIMEOUT)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                log.error("Chat provider returned status {}", response.statusCode());
                return degradedResponse();
            }
            return extractText(response.body());
        } catch (Exception e) {
            // Section 11, requirement 3: never surface an error page - return a static degraded response.
            log.error("Chat provider call failed", e);
            return degradedResponse();
        }
    }

    private String degradedResponse() {
        return "Sorry, the assistant is temporarily unavailable. Please browse FAQs or contact support.";
    }

    private String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
    }

    /** Minimal extraction to avoid pulling in a full JSON parser dependency just for this. */
    private String extractText(String rawJson) {
        int idx = rawJson.indexOf("\"text\"");
        if (idx == -1) return degradedResponse();
        int start = rawJson.indexOf('"', idx + 7) + 1;
        int end = rawJson.indexOf('"', start);
        if (start <= 0 || end <= start) return degradedResponse();
        return rawJson.substring(start, end).replace("\\n", "\n");
    }
}
