package com.moove.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet implementation class UserDashboardController
 * This controller handles requests for the user dashboard page
 */
@WebServlet("/UserDashboard")
public class UserDashboardController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    /**
     * Handles GET requests for the user dashboard and forwards to the JSP page
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Get the existing session, do not create a new one if it doesn't exist
        HttpSession session = request.getSession(false);
        
        // Check if session exists and contains a "username" attribute (user is logged in)
        if (session != null && session.getAttribute("username") != null) {
            // Retrieve user details from session
            String username = (String) session.getAttribute("username");
            String role = (String) session.getAttribute("role");
            
            // Set user details as request attributes to be used in the JSP page
            request.setAttribute("username", username);
            request.setAttribute("role", role);
            
            // Forward the request to the UserDashboard JSP page to display user info
            request.getRequestDispatcher("/WEB-INF/pages/UserDashboard.jsp").forward(request, response);
        } else {
            // If session does not exist or user is not logged in, redirect to Login page
            response.sendRedirect(request.getContextPath() + "/Login");
        }
    }

    /**
     * Processes POST requests by forwarding them to doGet method
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Redirect POST request handling to doGet for simplicity
        doGet(request, response);
    }
}
