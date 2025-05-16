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
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     * Handles GET requests for the user dashboard and forwards to the JSP page
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Get the user information from session
        HttpSession session = request.getSession(false);
        
        // If session exists, forward to dashboard page
        if (session != null && session.getAttribute("username") != null) {
            String username = (String) session.getAttribute("username");
            String role = (String) session.getAttribute("role");
            
            // Add user info to request attributes for use in JSP
            request.setAttribute("username", username);
            request.setAttribute("role", role);
            
            // Forward to the dashboard JSP page
            request.getRequestDispatcher("/WEB-INF/pages/UserDashboard.jsp").forward(request, response);
        } else {
            // If no session or no username in session, redirect to login
            response.sendRedirect(request.getContextPath() + "/Login");
        }
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     * Processes POST requests by redirecting to doGet
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}