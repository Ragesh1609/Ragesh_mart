package com.ragesh.rageshmart.filter;

import com.ragesh.rageshmart.dto.ApiResponse;
import com.ragesh.rageshmart.model.User;
import com.ragesh.rageshmart.util.JsonUtil;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Set;

/**
 * Section 9 security checklist: every protected servlet enforces session checks
 * via AuthFilter. Public endpoints (auth, product browsing, health) are excluded.
 */
@WebFilter("/api/v1/*")
public class AuthFilter implements Filter {

    private static final Set<String> PUBLIC_PATHS = Set.of(
            "/api/v1/auth/register",
            "/api/v1/auth/login",
            "/api/v1/health"
    );

    public static final String SESSION_USER_ATTR = "authUser";
    public static final String SESSION_ROLE_ATTR = "authRole";

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        String path = request.getRequestURI().substring(request.getContextPath().length());

        boolean isPublicGetProducts = request.getMethod().equals("GET") && path.startsWith("/api/v1/products");

        if (PUBLIC_PATHS.contains(path) || isPublicGetProducts) {
            chain.doFilter(req, res);
            return;
        }

        HttpSession session = request.getSession(false);
        if (session == null || session.getAttribute(SESSION_USER_ATTR) == null) {
            JsonUtil.writeJson(response, HttpServletResponse.SC_UNAUTHORIZED,
                    ApiResponse.fail("UNAUTHENTICATED", "Login required"));
            return;
        }

        chain.doFilter(req, res);
    }

    /** Convenience used by servlets to read the logged-in user id out of session. */
    public static Long currentUserId(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return null;
        User user = (User) session.getAttribute(SESSION_USER_ATTR);
        return user == null ? null : user.getId();
    }

    public static User currentUser(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return session == null ? null : (User) session.getAttribute(SESSION_USER_ATTR);
    }

    public static boolean hasRole(HttpServletRequest request, String role) {
        User user = currentUser(request);
        return user != null && user.getRole().name().equals(role);
    }
}
