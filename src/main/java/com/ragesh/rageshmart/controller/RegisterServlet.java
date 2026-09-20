package com.ragesh.rageshmart.controller;

import com.ragesh.rageshmart.dao.DAOFactory;
import com.ragesh.rageshmart.dto.ApiResponse;
import com.ragesh.rageshmart.dto.RegisterRequestDTO;
import com.ragesh.rageshmart.dto.UserResponseDTO;
import com.ragesh.rageshmart.exception.ValidationException;
import com.ragesh.rageshmart.model.User;
import com.ragesh.rageshmart.service.UserService;
import com.ragesh.rageshmart.service.impl.UserServiceImpl;
import com.ragesh.rageshmart.util.JsonUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/** F1: buyer/seller registration. Servlet stays thin - no SQL, no business rules. */
@WebServlet("/api/v1/auth/register")
public class RegisterServlet extends HttpServlet {

    private final UserService userService = new UserServiceImpl(DAOFactory.userDAO());

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            RegisterRequestDTO request = JsonUtil.readJson(req, RegisterRequestDTO.class);
            User user = userService.register(request);
            JsonUtil.writeJson(resp, HttpServletResponse.SC_CREATED, ApiResponse.ok(UserResponseDTO.from(user)));
        } catch (ValidationException e) {
            JsonUtil.writeJson(resp, HttpServletResponse.SC_BAD_REQUEST,
                    ApiResponse.fail("VALIDATION_ERROR", e.getField() + ": " + e.getMessage()));
        } catch (Exception e) {
            JsonUtil.writeJson(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    ApiResponse.fail("SERVER_ERROR", "Registration failed"));
        }
    }
}
