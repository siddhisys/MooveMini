package com.moove.util;

import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class RedirectionUtil {

    // Base URL for WEB-INF pages (not directly accessible via browser)
    private static final String baseUrl = "/WEB-INF/pages/";
    
    // Paths for internal forwarding (using RequestDispatcher)
    public static final String registerUrl = baseUrl + "Register.jsp";
    public static final String dashboardUrl = baseUrl + "admin/dashboard.jsp";
    public static final String loginUrl = baseUrl + "Login.jsp";
    public static final String homeUrl = baseUrl + "home.jsp";
    
    // Paths for external redirection (using sendRedirect)
    // These should be servlet mappings, not direct JSP paths
    private static final String baseServletUrl = "/";
    public static final String loginServletUrl = baseServletUrl + "Login";
    public static final String registerServletUrl = baseServletUrl + "Register";
    public static final String dashboardServletUrl = baseServletUrl + "Dashboard";
    public static final String homeServletUrl = baseServletUrl + "Home";

    /**
     * Sets an attribute in the request
     */
    public static void setMsgAttribute(HttpServletRequest req, String msgType, String msg) {
        req.setAttribute(msgType, msg);
    }

    /**
     * Forwards to a JSP page within WEB-INF
     * Use this when you need to forward to a JSP inside WEB-INF
     */
    public static void forwardToPage(HttpServletRequest req, HttpServletResponse resp, String page)
            throws ServletException, IOException {
        req.getRequestDispatcher(page).forward(req, resp);
    }
    
    /**
     * Redirects to a servlet URL (not a direct JSP path)
     * Use this when you need to redirect after POST (PRG pattern)
     */
    public void redirectToServlet(HttpServletRequest req, HttpServletResponse resp, String servletUrl)
            throws IOException {
        resp.sendRedirect(req.getContextPath() + servletUrl);
    }

    /**
     * Sets a message in the session and redirects to a servlet
     */
    public void setMsgAndRedirect(HttpServletRequest req, HttpServletResponse resp, String msgType, String msg, String targetUrl)
            throws IOException, ServletException {
        // Store message in session so it persists through the redirect
        req.getSession().setAttribute(msgType, msg);
        
        // Check if the URL is for a WEB-INF resource
        if (targetUrl.startsWith("/WEB-INF/")) {
            // For WEB-INF paths, use forward instead of redirect
            req.getRequestDispatcher(targetUrl).forward(req, resp);
        } else {
            // For servlet paths, use redirect
            resp.sendRedirect(req.getContextPath() + targetUrl);
        }
    }
}