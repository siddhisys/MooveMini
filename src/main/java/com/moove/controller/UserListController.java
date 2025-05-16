package com.moove.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.moove.model.UsersModel;
import com.moove.service.UserListService;

import java.io.File;
import java.io.IOException;
import java.util.List;

@WebServlet("/UserList")
public class UserListController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private UserListService userService;
    
    @Override
    public void init() throws ServletException {
        super.init();
        userService = new UserListService();
    }
    
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            System.out.println("Attempting to retrieve users...");
            List<UsersModel> users = userService.getAllUsers();
            
            if (users == null || users.isEmpty()) {
                response.getWriter().println("No users found in the database.");
                return;
            }

            System.out.println("Found " + users.size() + " users");
            request.setAttribute("userList", users);
            
            // Verify the JSP path exists
            String jspPath = getServletContext().getRealPath("/WEB-INF/pages/UserList.jsp");
            System.out.println("JSP path: " + jspPath);
            
            if (jspPath == null || !new File(jspPath).exists()) {
                System.out.println("JSP file not found at: " + jspPath);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "JSP file not found");
                return;
            }
            
            request.getRequestDispatcher("/WEB-INF/pages/UserList.jsp").forward(request, response);
            
        } catch (Exception e) {
            System.err.println("Error in doGet:");
            e.printStackTrace();
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().println("<h2>Error loading user list</h2>");
            response.getWriter().println("<p>" + e.getMessage() + "</p>");

        }
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // If you need to implement user search or other functionality in the future,
        // you can add it here
        
        // For now, just redirect to the GET handler
        doGet(request, response);
    }
    
    @Override
    public void destroy() {
        if (userService != null) {
            userService.close();
        }
        super.destroy();
    }
}