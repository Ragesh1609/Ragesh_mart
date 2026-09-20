package com.ragesh.rageshmart.util;

/**
 * Fallback escaping for any place JSTL fn:escapeXml / c:out is not used
 * (e.g. building HTML fragments in Java or JS-rendered text). JSP views
 * should still prefer <c:out> or fn:escapeXml directly.
 */
public final class HtmlEscapeUtil {

    private HtmlEscapeUtil() { }

    public static String escape(String input) {
        if (input == null) return "";
        StringBuilder sb = new StringBuilder(input.length());
        for (char c : input.toCharArray()) {
            switch (c) {
                case '&': sb.append("&amp;"); break;
                case '<': sb.append("&lt;"); break;
                case '>': sb.append("&gt;"); break;
                case '"': sb.append("&quot;"); break;
                case '\'': sb.append("&#x27;"); break;
                case '/': sb.append("&#x2F;"); break;
                default: sb.append(c);
            }
        }
        return sb.toString();
    }
}
