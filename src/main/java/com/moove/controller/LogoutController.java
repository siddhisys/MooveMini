package com.moove.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Cookie;

import java.io.IOException;

/**
 * Handles user logout requests by invalidating the session,
 * clearing relevant cookies, and redirecting to the login page.
 */
@WebServlet("/Logout")
public class LogoutController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * Handles GET requests to log out the user.
     * Invalidates the session, clears authentication cookies,
     * and redirects to the login page with a logout message.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Prevent browser caching to avoid back-button issues after logout
        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);

        // Invalidate the existing session if present
        HttpSession session = request.getSession(false);
        if (session != null) {
            System.out.println("Session exists, invalidating... Attributes before: " + session.getAttributeNames());
            session.invalidate();
            System.out.println("Session invalidated.");
        } else {
            System.out.println("No session exists to invalidate.");
        }
        
        // Clear cookies related to authentication (username and role)
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                if ("username".equals(cookie.getName()) || "role".equals(cookie.getName())) {
                    System.out.println("Clearing cookie: " + cookie.getName());
                    cookie.setMaxAge(0);  // Expire the cookie immediately
                    cookie.setPath(request.getContextPath());  // Ensure correct path for deletion
                    response.addCookie(cookie);
                }
            }
        }
        
        // Redirect user to login page with logout confirmation message
        String redirectUrl = request.getContextPath() + "/Login?message=loggedOut";
        System.out.println("Redirecting to: " + redirectUrl);
        response.sendRedirect(redirectUrl);
    }

    // Delegate POST requests to GET handler for logout
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);
    }
}
