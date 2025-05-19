package com.moove.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;

@WebFilter(asyncSupported = true, urlPatterns = "/*")
public class AuthenticationFilter implements Filter {

    private static final String LOGIN = "/Login";
    private static final String REGISTER = "/Register";
    private static final String HOME = "/home";
    private static final String ROOT = "/";
    private static final String ADMINDASHBOARD = "/AdminDashboard";
    private static final String USERLIST = "/UserList";
    private static final String PROGRAMLIST = "/programlist";
    private static final String USERPROFILE = "/profile";
    private static final String USERPROFILE_DELETE = "/profile/delete"; 
    private static final String USERDASHBOARD = "/UserDashboard";
    private static final String CONTACT = "/contact";
    private static final String SEARCH = "/SearchController";
    private static final String ABOUT = "/aboutus";
    private static final String ADDPROGRAM = "/AddProgram";
    private static final String GETPROGRAM = "/GetProgram";
    private static final String EDITPROGRAM = "/EditProgram";
    private static final String DELETEPROGRAM = "/DeleteProgram";
    private static final String LISTPROGRAM = "/ListPrograms";
    private static final String ENROLLMENT = "/EnrollmentController";
    private static final String LOGOUT = "/Logout";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialization if needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();

        // For debugging
        System.out.println("Auth Filter - Request URI: " + uri);
        System.out.println("Auth Filter - Context Path: " + contextPath);

        // Normalize the URI by removing the context path
        if (contextPath.length() > 0 && uri.startsWith(contextPath)) {
            uri = uri.substring(contextPath.length());
        }

        // Allow static resources
        if (uri.matches(".*(\\.css|\\.js|\\.png|\\.jpg|\\.jpeg|\\.gif|\\.woff|\\.ttf|\\.svg)$")) {
            chain.doFilter(request, response);
            return;
        }

        // Always allow access to login, register, logout, and root pages
        if (uri.equals(LOGIN) || uri.equals(REGISTER) || uri.equals(ROOT) || uri.equals("") || uri.equals(LOGOUT)) {
            chain.doFilter(request, response);
            return;
        }

        // Check session for logged-in state
        HttpSession session = req.getSession(false);
        boolean isLoggedIn = session != null && session.getAttribute("loggedInUser") != null;
        String role = session != null ? (String) session.getAttribute("role") : null;

        // For debugging
        System.out.println("Auth Filter - Is logged in (session): " + isLoggedIn);
        System.out.println("Auth Filter - Role (session): " + role);
        System.out.println("Auth Filter - Normalized URI: " + uri);

        // Admin role logic
        if (isLoggedIn && "Admin".equalsIgnoreCase(role)) {
            if (uri.equals(ADMINDASHBOARD) || uri.equals(USERLIST) || uri.equals(PROGRAMLIST)
                    || uri.equals(USERPROFILE) || uri.equals(HOME) || uri.equals(ADDPROGRAM) 
                    || uri.equals(GETPROGRAM) || uri.equals(EDITPROGRAM) || uri.equals(DELETEPROGRAM) 
                    || uri.equals(LISTPROGRAM) || uri.equals(LOGOUT)) {
                chain.doFilter(request, response);
            } else {
                System.out.println("Auth Filter - Admin user, redirecting to AdminDashboard");
                res.sendRedirect(contextPath + ADMINDASHBOARD);
            }
        }
        // Other logged-in users
        else if (isLoggedIn && ("Student".equalsIgnoreCase(role) || "Instructor".equalsIgnoreCase(role) || "Parent".equalsIgnoreCase(role))) {
            if (uri.equals(USERDASHBOARD) || uri.equals(HOME) || uri.equals(ABOUT) 
                    || uri.equals(SEARCH) || uri.equals(CONTACT) || uri.equals(USERPROFILE) 
                    || uri.equals(USERPROFILE_DELETE) || uri.equals(ENROLLMENT) || uri.equals(LOGOUT)) {
                chain.doFilter(request, response);
            } else {
                System.out.println("Auth Filter - Non-Admin user, redirecting to UserDashboard");
                res.sendRedirect(contextPath + USERDASHBOARD);
            }
        }
        // Not logged in
        else {
            System.out.println("Auth Filter - Not logged in, redirecting to Login");
            res.sendRedirect(contextPath + LOGIN);
        }
    }

    @Override
    public void destroy() {
        // Cleanup if needed
    }
}