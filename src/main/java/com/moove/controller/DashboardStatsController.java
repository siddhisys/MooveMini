package com.moove.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;

import com.moove.config.DbConfig;
import com.moove.service.AdminDashboardService;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/api/dashboard-stats")
public class DashboardStatsController extends HttpServlet {
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
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Map<String, Object> stats = adminDashboardService.getDashboardStats();
        StringBuilder json = new StringBuilder("{");
        json.append("\"totalStudents\":").append(stats.get("totalStudents")).append(",");
        json.append("\"totalPrograms\":").append(stats.get("totalPrograms")).append(",");
        json.append("\"totalInstructors\":").append(stats.get("totalInstructors")).append(",");
        json.append("\"programLevels\":{");
        Map<String, Integer> levels = (Map<String, Integer>) stats.get("programLevels");
        json.append("\"beginner\":").append(levels.get("beginner")).append(",");
        json.append("\"intermediate\":").append(levels.get("intermediate")).append(",");
        json.append("\"advanced\":").append(levels.get("advanced"));
        json.append("},");
        json.append("\"activeUsers\":{");
        Map<String, Integer> users = (Map<String, Integer>) stats.get("activeUsers");
        json.append("\"active\":").append(users.get("active")).append(",");
        json.append("\"inactive\":").append(users.get("inactive")).append(",");
        json.append("\"suspended\":").append(users.get("suspended"));
        json.append("}}");

        PrintWriter out = response.getWriter();
        out.print(json.toString());
        out.flush();
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