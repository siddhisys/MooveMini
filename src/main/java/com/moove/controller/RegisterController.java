package com.moove.controller;

import com.moove.model.UsersModel;
import com.moove.util.ImageUtil;
import com.moove.util.PasswordUtil;
import com.moove.util.RedirectionUtil;
import com.moove.util.ValidationUtil;
import com.moove.service.RegisterService;

import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet("/Register")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,  // 2MB
    maxFileSize = 1024 * 1024 * 10,       // 10MB
    maxRequestSize = 1024 * 1024 * 50     // 50MB
)
public class RegisterController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private ImageUtil imageUtil;
    private RegisterService registerService;
    private RedirectionUtil redirectionUtil;

    @Override
    public void init() throws ServletException {
        this.registerService = new RegisterService();
        this.imageUtil = new ImageUtil();
        this.redirectionUtil = new RedirectionUtil();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher(RedirectionUtil.registerUrl).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            System.out.println("Register form submitted - processing registration");

            String validationMessage = validateRegistrationForm(req);
            if (validationMessage != null) {
                handleError(req, resp, validationMessage);
                return;
            }

            String username = req.getParameter("username");
            String email = req.getParameter("email");
            
            if (registerService.usernameExists(username)) {
                handleError(req, resp, "Username already exists. Please choose a different username.");
                return;
            }
            
            if (registerService.emailExists(email)) {
                handleError(req, resp, "Email already exists. Please use a different email.");
                return;
            }

            UsersModel user = extractUserModel(req);

            boolean isRegistered = registerService.registerUser(user);
            if (isRegistered) {
                redirectionUtil.setMsgAndRedirect(req, resp, "success",
                    "Your account is successfully created!", RedirectionUtil.loginUrl);
            } else {
                redirectionUtil.setMsgAndRedirect(req, resp, "error",
                    "Could not register your account. Please try again later!", RedirectionUtil.registerUrl);
            }
        } catch (Exception e) {
            e.printStackTrace();
            redirectionUtil.setMsgAndRedirect(req, resp, "error",
                "An unexpected error occurred. Please try again.", RedirectionUtil.registerUrl);
        }
    }

    private String validateRegistrationForm(HttpServletRequest req) {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String email = req.getParameter("email");
        String address = req.getParameter("address");
        String gender = req.getParameter("gender");
        String role = req.getParameter("role");

        if (ValidationUtil.isNullOrEmpty(username)) return "Username is required.";
        if (ValidationUtil.isNullOrEmpty(password)) return "Password is required.";
        if (ValidationUtil.isNullOrEmpty(email)) return "Email is required.";
        if (ValidationUtil.isNullOrEmpty(address)) return "Address is required.";
        if (ValidationUtil.isNullOrEmpty(gender)) return "Gender is required.";
        if (ValidationUtil.isNullOrEmpty(role)) return "Role is required.";

        if (!ValidationUtil.isValidUsername(username)) return "Username must start with a letter and contain 5-20 alphanumeric characters.";
        if (!ValidationUtil.isValidPassword(password)) return "Password must have at least 8 chars, with 1 capital letter, 1 number, 1 special character (@$!%*?&).";
        if (!ValidationUtil.isValidEmail(email)) return "Invalid email format.";
        if (!ValidationUtil.isValidAddress(address)) return "Address should be at least 5 characters long.";
        if (!ValidationUtil.isValidGender(gender)) return "Gender must be one of: male, female, other.";
        if (!ValidationUtil.isValidRole(role)) return "Role must be one of: Instructor, Student, Parent, Admin";

        try {
            Part image = req.getPart("imageUrl");
            if (!ValidationUtil.isValidImageExtension(image)) {
                return "Invalid image format. Only jpg, jpeg, png, and gif are allowed.";
            }
        } catch (IOException | ServletException e) {
            return "Error handling image file. Please ensure the file is valid.";
        }

        return null;
    }

    private UsersModel extractUserModel(HttpServletRequest req) throws Exception {
        UsersModel user = new UsersModel();

        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String email = req.getParameter("email");
        String address = req.getParameter("address");
        String gender = req.getParameter("gender");
        String role = req.getParameter("role");

        // Handle image upload
        Part image = req.getPart("imageUrl");
        String imageName = imageUtil.getImageNameFromPart(image);
        String rootPath = "C:\\Users\\Siddhi\\eclipse-workspace\\Moove\\src\\main\\webapp\\resources\\images/";
        String uploadSubdirectory = "user";
        boolean uploadSuccess = imageUtil.uploadImage(image, rootPath, uploadSubdirectory);

        System.out.println("Image Name: " + imageName);
        System.out.println("Upload Subdirectory: " + uploadSubdirectory);
        System.out.println("Image upload success: " + uploadSuccess);

        user.setUser_Name(username);
        user.setUser_Email(email);
        user.setUser_Status("active");
        user.setUser_Address(address);
        user.setUser_Gender(gender);
        user.setImage_path(imageName); // Store only the filename, e.g., example.jpg

        String encryptedPassword = PasswordUtil.encrypt(username, password);
        user.setPassword(encryptedPassword != null ? encryptedPassword : password);

        int roleId = registerService.getRoleIdByName(role);
        user.setRole_Id(roleId);

        return user;
    }

    private void handleError(HttpServletRequest req, HttpServletResponse resp, String message)
            throws ServletException, IOException {
        req.setAttribute("error", message);
        req.setAttribute("username", req.getParameter("username"));
        req.setAttribute("email", req.getParameter("email"));
        req.setAttribute("address", req.getParameter("address"));
        req.setAttribute("gender", req.getParameter("gender"));
        req.setAttribute("role", req.getParameter("role"));
        req.getRequestDispatcher("/WEB-INF/pages/Register.jsp").forward(req, resp);
    }
}