package com.moove.controller;

import java.io.File;
import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

import com.moove.model.UsersModel;
import com.moove.service.RegisterService;
import com.moove.service.LoginService;
import com.moove.util.PasswordUtil;

/**
 * Servlet to handle user profile viewing, updating, and deletion.
 * Supports multipart file uploads for profile image updates.
 */
@WebServlet({"/profile", "/profile/delete"})
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,    // 1 MB threshold before file is written to disk
    maxFileSize = 1024 * 1024 * 10,     // Max single file size 10 MB
    maxRequestSize = 1024 * 1024 * 15   // Max total request size 15 MB
)
public class ProfileController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private RegisterService userService = new RegisterService();
    private LoginService loginService = new LoginService();

    /**
     * Handles GET requests to display the user profile page.
     * Loads the logged-in user's data, verifies profile image existence,
     * generates a CSRF token, and forwards to the JSP for display.
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        UsersModel loggedInUser = (UsersModel) session.getAttribute("loggedInUser");

        if (loggedInUser != null) {
            // Prepare user data for display (exclude password for security)
            UsersModel userForDisplay = new UsersModel();
            userForDisplay.setUser_ID(loggedInUser.getUser_ID());
            userForDisplay.setUser_Name(loggedInUser.getUser_Name());
            userForDisplay.setUser_Email(loggedInUser.getUser_Email());
            userForDisplay.setUser_Address(loggedInUser.getUser_Address());
            userForDisplay.setImage_path(loggedInUser.getImage_path());

            // Check if user profile image file exists; use default if missing
            if (userForDisplay.getImage_path() != null && !userForDisplay.getImage_path().isEmpty()) {
                String applicationPath = request.getServletContext().getRealPath("");
                String imageFullPath = applicationPath + "resources" + File.separator + "images" + File.separator + "user" + File.separator + userForDisplay.getImage_path();
                File imageFile = new File(imageFullPath);
                if (!imageFile.exists()) {
                    userForDisplay.setImage_path("default_avatar.png");
                }
            } else {
                userForDisplay.setImage_path("default_avatar.png");
            }

            // Generate a CSRF token and store it in session and request for form security
            String csrfToken = java.util.UUID.randomUUID().toString();
            session.setAttribute("csrfToken", csrfToken);
            request.setAttribute("csrfToken", csrfToken);

            // Set user attribute and forward to the profile JSP page
            request.setAttribute("user", userForDisplay);
            request.getRequestDispatcher("/WEB-INF/pages/UserProfile.jsp").forward(request, response);
        } else {
            // If no logged-in user, redirect to login page
            response.sendRedirect(request.getContextPath() + "/Login");
        }
    }

    /**
     * Handles POST requests to update user profile or delete the user account.
     * Validates CSRF token, handles file upload for profile image, encrypts password,
     * and updates user info or deletes the user as requested.
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        UsersModel loggedInUser = (UsersModel) session.getAttribute("loggedInUser");

        // Redirect to login if user is not authenticated
        if (loggedInUser == null) {
            response.sendRedirect(request.getContextPath() + "/Login");
            return;
        }

        String servletPath = request.getServletPath();

        // Validate CSRF token to prevent cross-site request forgery
        String submittedToken = request.getParameter("csrfToken");
        String sessionToken = (String) session.getAttribute("csrfToken");
        if (submittedToken == null || !submittedToken.equals(sessionToken)) {
            request.setAttribute("errorMessage", "Invalid CSRF token.");
            request.setAttribute("user", loggedInUser);
            request.getRequestDispatcher("/WEB-INF/pages/UserProfile.jsp").forward(request, response);
            return;
        }

        // Handle account deletion request
        if ("/profile/delete".equals(servletPath)) {
            boolean deleted = userService.deleteUser(loggedInUser.getUser_ID());
            if (deleted) {
                session.invalidate(); // Invalidate session after account deletion
                response.sendRedirect(request.getContextPath() + "/Login?message=accountDeleted");
            } else {
                request.setAttribute("errorMessage", "Failed to delete account.");
                request.setAttribute("user", loggedInUser);
                request.getRequestDispatcher("/WEB-INF/pages/UserProfile.jsp").forward(request, response);
            }
            return;
        }

        // Profile update request processing
        String newUsername = request.getParameter("firstName");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String address = request.getParameter("address");

        // Check if the new username is already taken (if changed)
        if (!newUsername.equals(loggedInUser.getUser_Name()) && userService.usernameExists(newUsername)) {
            request.setAttribute("errorMessage", "Username already taken. Please choose another.");
            request.setAttribute("user", loggedInUser);
            request.getRequestDispatcher("/WEB-INF/pages/UserProfile.jsp").forward(request, response);
            return;
        }

        // Encrypt new password if provided; otherwise, keep existing encrypted password
        String encryptedPassword = loggedInUser.getPassword();
        if (password != null && !password.trim().isEmpty()) {
            try {
                encryptedPassword = PasswordUtil.encrypt(password);
            } catch (RuntimeException e) {
                System.out.println("Password encryption failed: " + e.getMessage());
                request.setAttribute("errorMessage", "Failed to encrypt password. Please try again.");
                request.setAttribute("user", loggedInUser);
                request.getRequestDispatcher("/WEB-INF/pages/UserProfile.jsp").forward(request, response);
                return;
            }
        }

        // Update user object with new details
        loggedInUser.setUser_Name(newUsername);
        loggedInUser.setUser_Email(email);
        loggedInUser.setPassword(encryptedPassword);
        loggedInUser.setUser_Address(address);

        // Handle profile image upload if a file is provided
        try {
            Part filePart = request.getPart("profileImage");
            if (filePart != null && filePart.getSize() > 0) {
                // Extract file extension
                String fileName = getSubmittedFileName(filePart);
                String fileExtension = fileName.substring(fileName.lastIndexOf("."));
                // Create unique file name to avoid conflicts
                String newFileName = "user_" + loggedInUser.getUser_ID() + "_" + System.currentTimeMillis() + fileExtension;

                // Define upload directory and create if it doesn't exist
                String applicationPath = request.getServletContext().getRealPath("");
                String uploadPath = applicationPath + "resources" + File.separator + "images" + File.separator + "user";
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }

                // Write the uploaded file to disk
                String filePath = uploadPath + File.separator + newFileName;
                filePart.write(filePath);

                // Update user's profile image path
                loggedInUser.setImage_path(newFileName);
                System.out.println("Profile image uploaded to: " + filePath);
            }
        } catch (Exception e) {
            System.out.println("Error processing file upload: " + e.getMessage());
            request.setAttribute("errorMessage", "Failed to upload profile image: " + e.getMessage());
        }

        // Update user profile in database
        boolean success = userService.updateUserProfile(loggedInUser);

        // Refresh user details from database after update
        UsersModel refreshedUser = success ? loginService.getUserByUsername(newUsername) : null;

        // Update session and request attributes accordingly
        if (refreshedUser != null) {
            session.setAttribute("loggedInUser", refreshedUser);
            session.setAttribute("username", newUsername);
            request.setAttribute("user", refreshedUser);
        } else {
            session.setAttribute("loggedInUser", loggedInUser);
            session.setAttribute("username", newUsername);
            request.setAttribute("user", loggedInUser);
        }

        // Provide feedback message for success or failure of update
        request.setAttribute(success ? "successMessage" : "errorMessage",
                success ? "Profile updated successfully!" : "Failed to update profile. Please try again.");

        // Forward back to profile page
        request.getRequestDispatcher("/WEB-INF/pages/UserProfile.jsp").forward(request, response);
    }

    /**
     * Utility method to extract the submitted file name from the Part header.
     */
    private String getSubmittedFileName(Part part) {
        for (String cd : part.getHeader("content-disposition").split(";")) {
            if (cd.trim().startsWith("filename")) {
                String fileName = cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
                // Handle IE and Unix-based browsers file paths
                return fileName.substring(fileName.lastIndexOf('/') + 1).substring(fileName.lastIndexOf('\\') + 1);
            }
        }
        return null;
    }
}
