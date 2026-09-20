package com.ragesh.rageshmart.chat;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/** Selects the ChatProvider implementation via config flag ai.chatbot.provider=gemini|mock. */
public final class ChatProviderFactory {

    private ChatProviderFactory() { }

    public static ChatProvider create() {
        Properties props = new Properties();
        try (InputStream in = ChatProviderFactory.class.getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (in != null) props.load(in);
        } catch (IOException ignored) {
            // fall through to mock default below
        }

        String provider = props.getProperty("ai.chatbot.provider", "mock");
        if ("gemini".equalsIgnoreCase(provider)) {
            String apiKey = System.getenv("GEMINI_API_KEY");
            return new GeminiChatProvider(apiKey);
        }
        return new MockChatProvider();
    }
}
