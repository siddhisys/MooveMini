package com.moove.controller;

import java.io.IOException;
import java.io.File;
import com.moove.model.UsersModel;
import com.moove.service.RegisterService;
import com.moove.service.LoginService;
import com.moove.util.PasswordUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

@WebServlet("/profile")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024, // 1 MB
    maxFileSize = 1024 * 1024 * 10, // 10 MB
    maxRequestSize = 1024 * 1024 * 15 // 15 MB
)
public class ProfileController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private RegisterService userService = new RegisterService();
    private LoginService loginService = new LoginService();

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        UsersModel loggedInUser = (UsersModel) session.getAttribute("loggedInUser");
        
        if (loggedInUser != null) {
            UsersModel userForDisplay = new UsersModel();
            userForDisplay.setUser_ID(loggedInUser.getUser_ID());
            userForDisplay.setUser_Name(loggedInUser.getUser_Name());
            userForDisplay.setUser_Email(loggedInUser.getUser_Email());
            userForDisplay.setUser_Address(loggedInUser.getUser_Address());
            userForDisplay.setImage_path(loggedInUser.getImage_path());
            
            try {
                String decryptedPassword = PasswordUtil.decrypt(loggedInUser.getPassword(), loggedInUser.getUser_Name());
                userForDisplay.setPassword(decryptedPassword != null ? decryptedPassword : "");
            } catch (Exception e) {
                System.out.println("Password decryption failed: " + e.getMessage());
                userForDisplay.setPassword("");
            }

            System.out.println("Profile GET - Image Path from DB: " + userForDisplay.getImage_path());
            
            if (userForDisplay.getImage_path() != null && !userForDisplay.getImage_path().isEmpty()) {
                String applicationPath = request.getServletContext().getRealPath("");
                String imageFullPath = applicationPath + "resources" + File.separator + "images" + File.separator + "user" + File.separator + userForDisplay.getImage_path();
                File imageFile = new File(imageFullPath);
                
                if (!imageFile.exists()) {
                    System.out.println("WARNING: Profile image file does not exist at: " + imageFullPath);
                    userForDisplay.setImage_path("default_avatar.png");
                }
            } else {
                userForDisplay.setImage_path("default_avatar.png");
            }

            System.out.println("Profile GET - Final Image Path: " + userForDisplay.getImage_path());
            
            request.setAttribute("user", userForDisplay);
            request.getRequestDispatcher("/WEB-INF/pages/UserProfile.jsp").forward(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/Login");
        }
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        UsersModel loggedInUser = (UsersModel) session.getAttribute("loggedInUser");
        
        if (loggedInUser != null) {
            String newUsername = request.getParameter("firstName");
            String email = request.getParameter("email");
            String password = request.getParameter("password");
            String address = request.getParameter("address");
            
            if (!newUsername.equals(loggedInUser.getUser_Name())) {
                if (userService.usernameExists(newUsername)) {
                    request.setAttribute("errorMessage", "Username already taken. Please choose another.");
                    try {
                        String decryptedPassword = PasswordUtil.decrypt(loggedInUser.getPassword(), loggedInUser.getUser_Name());
                        loggedInUser.setPassword(decryptedPassword != null ? decryptedPassword : "");
                    } catch (Exception e) {
                        loggedInUser.setPassword("");
                    }
                    request.setAttribute("user", loggedInUser);
                    request.getRequestDispatcher("/WEB-INF/pages/UserProfile.jsp").forward(request, response);
                    return;
                }
            }
            
            loggedInUser.setUser_Name(newUsername);
            loggedInUser.setUser_Email(email);
            loggedInUser.setPassword(password);
            loggedInUser.setUser_Address(address);
            
            try {
                Part filePart = request.getPart("profileImage");
                if (filePart != null && filePart.getSize() > 0) {
                    String fileName = getSubmittedFileName(filePart);
                    String fileExtension = fileName.substring(fileName.lastIndexOf("."));
                    String newFileName = "user_" + loggedInUser.getUser_ID() + "_" + System.currentTimeMillis() + fileExtension;
                    
                    String applicationPath = request.getServletContext().getRealPath("");
                    String uploadPath = applicationPath + "resources" + File.separator + "images" + File.separator + "user";
                    File uploadDir = new File(uploadPath);
                    if (!uploadDir.exists()) {
                        uploadDir.mkdirs();
                    }
                    
                    String filePath = uploadPath + File.separator + newFileName;
                    filePart.write(filePath);
                    loggedInUser.setImage_path(newFileName); // Store only the filename, e.g., user_1_123456789.jpg
                    System.out.println("Profile image uploaded to: " + filePath);
                }
            } catch (Exception e) {
                System.out.println("Error processing file upload: " + e.getMessage());
                request.setAttribute("errorMessage", "Failed to upload profile image: " + e.getMessage());
            }
            
            boolean success = userService.updateUserProfile(loggedInUser);
            UsersModel refreshedUser = success ? loginService.getUserByUsername(newUsername) : null;
            if (refreshedUser != null) {
                session.setAttribute("loggedInUser", refreshedUser);
                session.setAttribute("username", newUsername);
                request.setAttribute("user", refreshedUser);
            } else {
                session.setAttribute("loggedInUser", loggedInUser);
                session.setAttribute("username", newUsername);
                request.setAttribute("user", loggedInUser);
            }
            request.setAttribute(success ? "successMessage" : "errorMessage", 
                               success ? "Profile updated successfully!" : "Failed to update profile. Please try again.");
            request.getRequestDispatcher("/WEB-INF/pages/UserProfile.jsp").forward(request, response);
        } else {
            response.sendRedirect(request.getContextPath() + "/Login");
        }
    }

    private String getSubmittedFileName(Part part) {
        for (String cd : part.getHeader("content-disposition").split(";")) {
            if (cd.trim().startsWith("filename")) {
                String fileName = cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
                return fileName.substring(fileName.lastIndexOf('/') + 1).substring(fileName.lastIndexOf('\\') + 1);
            }
        }
        return null;
    }
    
    
}