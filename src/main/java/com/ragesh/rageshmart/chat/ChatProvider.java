package com.ragesh.rageshmart.chat;

/** Section 17, rule 1: AI provider accessed through an interface, not a hardcoded implementation. */
public interface ChatProvider {
    String getReply(String userMessage, String context);
}
