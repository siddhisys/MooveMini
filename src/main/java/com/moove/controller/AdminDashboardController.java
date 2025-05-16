package com.moove.controller;

import com.moove.config.DbConfig;
import com.moove.service.AdminDashboardService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

@WebServlet("/AdminDashboard")
public class AdminDashboardController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private AdminDashboardService adminDashboardService;

    @Override
    public void init() throws ServletException {
        try {
            Connection conn = DbConfig.getDbConnection();
            adminDashboardService = new AdminDashboardService(conn);
        } catch (SQLException | ClassNotFoundException e) {
            throw new ServletException("Database connection failed", e);
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");
        if (username == null) username = "Admin";

        request.setAttribute("username", username);
        request.setAttribute("stats", adminDashboardService.getDashboardStats());
        request.setAttribute("events", adminDashboardService.getEvents());

        request.getRequestDispatcher("/WEB-INF/pages/AdminDashboard.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    public void destroy() {
        if (adminDashboardService != null) {
            adminDashboardService.closeConnection();
        }
    }
}