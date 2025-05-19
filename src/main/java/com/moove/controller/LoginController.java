package com.moove.controller;

import java.io.IOException;
import com.moove.model.UsersModel;
import com.moove.service.LoginService;
import com.moove.util.CookieUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

/**
 * Handles user login requests.
 * Processes authentication and manages session and cookie data.
 * Redirects users to their respective dashboards upon successful login.
 */
@WebServlet(asyncSupported = true, urlPatterns = { "/Login" })
public class LoginController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final LoginService loginService;

    // Initialize the LoginService
    public LoginController() {
        this.loginService = new LoginService();
    }

    // Display the login page on GET request
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!response.isCommitted()) {
            request.getRequestDispatcher("/WEB-INF/pages/Login.jsp").forward(request, response);
        }
    }

    // Process login form submission on POST request
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        System.out.println("Login attempt for username: " + username);

        UsersModel userModel = new UsersModel(username, password);
        Boolean loginStatus = loginService.authenticateUser(userModel);

        if (loginStatus != null && loginStatus) {
            System.out.println("✅ Login successful for user: " + username);

            // Retrieve full user profile data
            UsersModel completeUserData = loginService.getUserByUsername(username);

            // Create new session and store user info
            HttpSession session = req.getSession(true);
            session.setAttribute("username", username);
            session.setAttribute("loggedInUser", completeUserData);

            // Set cookies for username and role, expire in 30 minutes
            CookieUtil.addCookie(resp, "username", username, 60 * 30);

            String role = "Student"; // Default role
            if ("admin".equalsIgnoreCase(username)) {
                role = "Admin";
                session.setAttribute("role", role);
                CookieUtil.addCookie(resp, "role", role, 60 * 30);
                resp.sendRedirect(req.getContextPath() + "/AdminDashboard");
            } else {
                session.setAttribute("role", role);
                CookieUtil.addCookie(resp, "role", role, 60 * 30);
                resp.sendRedirect(req.getContextPath() + "/UserDashboard");
            }
        } else {
            System.out.println("❌ Login failed for user: " + username);
            handleLoginFailure(req, resp, loginStatus);
        }
    }

    // Handle login failure by setting error message and forwarding back to login page
    private void handleLoginFailure(HttpServletRequest req, HttpServletResponse resp, Boolean loginStatus)
            throws ServletException, IOException {
        String errorMessage;
        if (loginStatus == null) {
            errorMessage = "Our server is under maintenance. Please try again later!";
        } else {
            errorMessage = "User credential mismatch. Please try again!";
        }
        req.setAttribute("error", errorMessage);
        req.getRequestDispatcher("/WEB-INF/pages/Login.jsp").forward(req, resp);
    }
}
