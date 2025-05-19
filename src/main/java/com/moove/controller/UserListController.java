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
import java.util.Map;

/**
 * Servlet controller to handle requests related to displaying the user list.
 */
@WebServlet("/UserList")
public class UserListController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    // Service used to fetch user data and associated programs
    private UserListService userService;
    
    /**
     * Initializes the servlet and sets up the user service
     */
    @Override
    public void init() throws ServletException {
        super.init();
        userService = new UserListService();  // Initialize the service layer
    }
    
    /**
     * Handles GET requests to display the list of users and their programs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            System.out.println("Attempting to retrieve users...");
            
            // Retrieve users and their associated program names
            Map<String, Object> result = userService.getAllUsersWithPrograms();
            
            // Extract the list of users from the result map
            @SuppressWarnings("unchecked")
            List<UsersModel> users = (List<UsersModel>) result.get("users");
            
            // Extract the map linking user IDs to their program names
            @SuppressWarnings("unchecked")
            Map<Integer, String> userProgramMap = (Map<Integer, String>) result.get("userProgramMap");
            
            // Handle case where no users are found
            if (users == null || users.isEmpty()) {
                response.getWriter().println("No users found in the database.");
                return;
            }

            System.out.println("Found " + users.size() + " users");
            
            // Set user list and program mapping as request attributes
            request.setAttribute("userList", users);
            request.setAttribute("userProgramMap", userProgramMap);
            
            // Get the actual file system path of the JSP
            String jspPath = getServletContext().getRealPath("/WEB-INF/pages/UserList.jsp");
            System.out.println("JSP path: " + jspPath);
            
            // Check if the JSP file exists before forwarding
            if (jspPath == null || !new File(jspPath).exists()) {
                System.out.println("JSP file not found at: " + jspPath);
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "JSP file not found");
                return;
            }
            
            // Forward request to the JSP page to display the user list
            request.getRequestDispatcher("/WEB-INF/pages/UserList.jsp").forward(request, response);
            
        } catch (Exception e) {
            // Handle any exceptions and display an error message to the user
            System.err.println("Error in doGet:");
            e.printStackTrace();
            response.setContentType("text/html;charset=UTF-8");
            response.getWriter().println("<h2>Error loading user list</h2>");
            response.getWriter().println("<p>" + e.getMessage() + "</p>");
        }
    }
    
    /**
     * Handles POST requests by redirecting to doGet
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        doGet(request, response);  // Use same logic as GET
    }
    
    /**
     * Cleans up resources when the servlet is destroyed
     */
    @Override
    public void destroy() {
        // Close the service connection if it was initialized
        if (userService != null) {
            userService.close();
        }
        super.destroy();
    }
}
