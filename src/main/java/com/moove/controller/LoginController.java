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
 * LoginController is responsible for handling login requests for the Moove application.
 * It processes authentication and redirects users to appropriate pages.
 */
@WebServlet(asyncSupported = true, urlPatterns = { "/Login" })
public class LoginController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final LoginService loginService;

    /**
     * Constructor initializes the LoginService.
     */
    public LoginController() {
        this.loginService = new LoginService();
    }

    /**
     * Handles GET requests to the login page.
     *
     * @param request  HttpServletRequest object
     * @param response HttpServletResponse object
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!response.isCommitted()) {
            request.getRequestDispatcher("/WEB-INF/pages/Login.jsp").forward(request, response);
        }
    }


    /**
     * Handles POST requests for user login.
     *
     * @param request  HttpServletRequest object
     * @param response HttpServletResponse object
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        System.out.println("Login attempt for username: " + username);

        UsersModel userModel = new UsersModel(username, password);
        Boolean loginStatus = loginService.authenticateUser(userModel);

        if (loginStatus != null && loginStatus) {
            System.out.println("✅ Login successful for user: " + username);
            
            // Get complete user object with all profile data
            UsersModel completeUserData = loginService.getUserByUsername(username);
            
            // Set session attributes
            HttpSession session = req.getSession(true);
            session.setAttribute("username", username);
            session.setAttribute("loggedInUser", completeUserData);  // Store the full user object
            
            // Set cookies
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
            handleLoginFailure(req, resp, loginStatus); // only forward happens here
        }
    }

    /**
     * Handles login failures by setting error messages and forwarding back to the login page.
     *
     * @param req         HttpServletRequest object
     * @param resp        HttpServletResponse object
     * @param loginStatus Boolean indicating the login status
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException      if an I/O error occurs
     */
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