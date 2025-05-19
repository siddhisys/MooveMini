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

// Servlet mapped to handle requests for the admin dashboard at URL "/AdminDashboard"
@WebServlet("/AdminDashboard")
public class AdminDashboardController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Service class instance that handles business logic and database interactions
    private AdminDashboardService adminDashboardService;

    // Initialization method called once when the servlet is first loaded
    @Override
    public void init() throws ServletException {
        try {
            // Get a database connection from DbConfig
            Connection conn = DbConfig.getDbConnection();

            // Initialize the service class with the database connection
            adminDashboardService = new AdminDashboardService(conn);
        } catch (SQLException | ClassNotFoundException e) {
            // If connection fails, throw ServletException to indicate servlet can't initialize properly
            throw new ServletException("Database connection failed", e);
        }
    }

    // Handles HTTP GET requests to display the admin dashboard page
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Get the current HTTP session
        HttpSession session = request.getSession();

        // Retrieve the username from session; default to "Admin" if not set
        String username = (String) session.getAttribute("username");
        if (username == null) username = "Admin";

        // Set username as a request attribute to be used in JSP page
        request.setAttribute("username", username);

        // Retrieve dashboard statistics data via service and set as request attribute
        request.setAttribute("stats", adminDashboardService.getDashboardStats());

        // Retrieve list of events via service and set as request attribute
        request.setAttribute("events", adminDashboardService.getEvents());

        // Forward the request to the JSP page that renders the admin dashboard UI
        request.getRequestDispatcher("/WEB-INF/pages/AdminDashboard.jsp").forward(request, response);
    }

    // Handles HTTP POST requests by delegating to doGet for simplicity
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }

    // Called when servlet is being destroyed, e.g., server shutdown or redeploy
    @Override
    public void destroy() {
        // Close database connection held by the service to free resources
        if (adminDashboardService != null) {
            adminDashboardService.closeConnection();
        }
    }
}
