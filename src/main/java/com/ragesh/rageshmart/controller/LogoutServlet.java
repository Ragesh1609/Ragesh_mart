package com.ragesh.rageshmart.controller;

import com.ragesh.rageshmart.dto.ApiResponse;
import com.ragesh.rageshmart.util.JsonUtil;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/api/v1/auth/logout")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        JsonUtil.writeJson(resp, HttpServletResponse.SC_OK, ApiResponse.ok(null));
    }
}
