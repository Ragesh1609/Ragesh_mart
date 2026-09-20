package com.ragesh.rageshmart.controller;

import com.ragesh.rageshmart.listener.DataSourceListener;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.Connection;

/** Section 18, rule 1: GET /api/v1/health verifies DB connectivity. Public endpoint. */
@WebServlet("/api/v1/health")
public class HealthServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String dbStatus;
        try (Connection conn = DataSourceListener.getConnection()) {
            dbStatus = conn.isValid(2) ? "UP" : "DOWN";
        } catch (Exception e) {
            dbStatus = "DOWN";
        }
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.setStatus(HttpServletResponse.SC_OK);
        resp.getWriter().write("{\"status\":\"UP\",\"db\":\"" + dbStatus + "\"}");
    }
}
