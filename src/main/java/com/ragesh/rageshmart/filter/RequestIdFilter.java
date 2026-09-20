package com.ragesh.rageshmart.filter;

import org.slf4j.MDC;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;
import java.util.UUID;

@WebFilter("/*")
public class RequestIdFilter implements Filter {

    public static final String REQUEST_ID_KEY = "requestId";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        String requestId = UUID.randomUUID().toString();
        MDC.put(REQUEST_ID_KEY, requestId);
        try {
            chain.doFilter(request, response);
        } finally {
            MDC.remove(REQUEST_ID_KEY);
        }
    }
}
