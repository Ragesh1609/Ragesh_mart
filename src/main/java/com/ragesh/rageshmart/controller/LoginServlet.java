package com.ragesh.rageshmart.controller;

import com.ragesh.rageshmart.dao.DAOFactory;
import com.ragesh.rageshmart.dto.ApiResponse;
import com.ragesh.rageshmart.dto.LoginRequestDTO;
import com.ragesh.rageshmart.dto.UserResponseDTO;
import com.ragesh.rageshmart.exception.AuthException;
import com.ragesh.rageshmart.exception.ValidationException;
import com.ragesh.rageshmart.filter.AuthFilter;
import com.ragesh.rageshmart.model.User;
import com.ragesh.rageshmart.service.UserService;
import com.ragesh.rageshmart.service.impl.UserServiceImpl;
import com.ragesh.rageshmart.util.JsonUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/api/v1/auth/login")
public class LoginServlet extends HttpServlet {

    private static final int SESSION_TIMEOUT_SECONDS = 30 * 60; // 30 minutes

    private final UserService userService = new UserServiceImpl(DAOFactory.userDAO());

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            LoginRequestDTO request = JsonUtil.readJson(req, LoginRequestDTO.class);
            User user = userService.login(request.getEmail(), request.getPassword());

            // Section 2, rule 3: regenerate session ID on login to prevent session fixation.
            HttpSession oldSession = req.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }
            HttpSession session = req.getSession(true);
            session.setMaxInactiveInterval(SESSION_TIMEOUT_SECONDS);
            session.setAttribute(AuthFilter.SESSION_USER_ATTR, user);
            session.setAttribute(AuthFilter.SESSION_ROLE_ATTR, user.getRole().name());

            JsonUtil.writeJson(resp, HttpServletResponse.SC_OK, ApiResponse.ok(UserResponseDTO.from(user)));
        } catch (ValidationException e) {
            JsonUtil.writeJson(resp, HttpServletResponse.SC_BAD_REQUEST,
                    ApiResponse.fail("VALIDATION_ERROR", e.getMessage()));
        } catch (AuthException e) {
            JsonUtil.writeJson(resp, e.getStatusCode(), ApiResponse.fail("AUTH_ERROR", e.getMessage()));
        } catch (Exception e) {
            JsonUtil.writeJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    ApiResponse.fail("SERVER_ERROR", "Login failed"));
        }
    }
}
