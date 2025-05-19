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

// Servlet mapped to handle requests for dashboard statistics at URL "/api/dashboard-stats"
@WebServlet("/api/dashboard-stats")
public class DashboardStatsController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Service class instance to get dashboard stats from the database
    private AdminDashboardService adminDashboardService;

    // Initialization method called once when the servlet is first loaded
    @Override
    public void init() throws ServletException {
        try {
            // Obtain a database connection from DbConfig
            Connection conn = DbConfig.getDbConnection();

            // Initialize the AdminDashboardService with the DB connection
            adminDashboardService = new AdminDashboardService(conn);
        } catch (SQLException | ClassNotFoundException e) {
            // If connection fails, throw ServletException to prevent servlet from initializing
            throw new ServletException("Database connection failed", e);
        }
    }

    // Handles HTTP GET requests to provide dashboard stats in JSON format
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Set the content type to JSON and encoding to UTF-8
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Retrieve dashboard statistics from the service as a Map
        Map<String, Object> stats = adminDashboardService.getDashboardStats();

        // Build a JSON string manually using StringBuilder
        StringBuilder json = new StringBuilder("{");

        // Append basic numeric stats: totalStudents, totalPrograms, totalInstructors
        json.append("\"totalStudents\":").append(stats.get("totalStudents")).append(",");
        json.append("\"totalPrograms\":").append(stats.get("totalPrograms")).append(",");
        json.append("\"totalInstructors\":").append(stats.get("totalInstructors")).append(",");

        // Append nested JSON object for programLevels
        json.append("\"programLevels\":{");
        @SuppressWarnings("unchecked")
        Map<String, Integer> levels = (Map<String, Integer>) stats.get("programLevels");
        json.append("\"beginner\":").append(levels.get("beginner")).append(",");
        json.append("\"intermediate\":").append(levels.get("intermediate")).append(",");
        json.append("\"advanced\":").append(levels.get("advanced"));
        json.append("},");

        // Append nested JSON object for activeUsers
        json.append("\"activeUsers\":{");
        @SuppressWarnings("unchecked")
        Map<String, Integer> users = (Map<String, Integer>) stats.get("activeUsers");
        json.append("\"active\":").append(users.get("active")).append(",");
        json.append("\"inactive\":").append(users.get("inactive")).append(",");
        json.append("\"suspended\":").append(users.get("suspended"));
        json.append("}}");

        // Get the PrintWriter to send response to the client
        PrintWriter out = response.getWriter();

        // Write the constructed JSON string to response
        out.print(json.toString());

        // Flush the writer to ensure all data is sent
        out.flush();
    }

    // Handles HTTP POST requests by delegating to doGet for simplicity
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    // Called when servlet is being destroyed (e.g., server shutdown or redeploy)
    @Override
    public void destroy() {
        // Close the database connection held by the service to free resources
        if (adminDashboardService != null) {
            adminDashboardService.closeConnection();
        }
    }
}
