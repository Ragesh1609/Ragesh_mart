package com.ragesh.rageshmart.chat;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Canned FAQ answers, no network calls. This is the default provider
 * (ai.chatbot.provider=mock) and also the degraded-response fallback if the
 * real provider's API call fails (Section 11, requirement 3).
 */
public class MockChatProvider implements ChatProvider {

    private static final Map<String, String> FAQ = new LinkedHashMap<>();
    static {
        FAQ.put("track", "You can see your order status under Order History once logged in.");
        FAQ.put("return", "Returns aren't automated yet - contact the seller from your order details page.");
        FAQ.put("payment", "Checkout uses a mock payment confirmation for this project; no real card is charged.");
        FAQ.put("shipping", "Estimated delivery windows appear on the order status page after checkout.");
        FAQ.put("stock", "Out-of-stock items are hidden from checkout automatically; try searching a similar item.");
        FAQ.put("account", "You can register as a Buyer or Seller from the login page.");
        FAQ.put("seller", "Sellers can list, edit, or delete products from the Seller Dashboard.");
        FAQ.put("cart", "Add items from any product page; the cart total updates automatically.");
        FAQ.put("review", "You can leave a star rating and review once an order is marked Delivered.");
        FAQ.put("price", "Product prices are set individually by each seller and shown on the listing.");
    }

    private static final String FALLBACK =
            "I can help with questions about orders, products, payments, and your account. Could you rephrase that?";

    @Override
    public String getReply(String userMessage, String context) {
        if (userMessage == null) return FALLBACK;
        String lower = userMessage.toLowerCase();
        for (Map.Entry<String, String> entry : FAQ.entrySet()) {
            if (lower.contains(entry.getKey())) {
                return entry.getValue();
            }
        }
        return FALLBACK;
    }
}
