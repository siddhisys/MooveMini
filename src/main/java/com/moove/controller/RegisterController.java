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
    fileSizeThreshold = 1024 * 1024 * 2,  // 2MB threshold after which files are written to disk
    maxFileSize = 1024 * 1024 * 10,       // Max file size = 10MB
    maxRequestSize = 1024 * 1024 * 50     // Max request size = 50MB
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
        // Forward GET request to the registration page
        request.getRequestDispatcher(RedirectionUtil.registerUrl).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            System.out.println("Register form submitted - processing registration");

            // Validate form fields and image upload
            String validationMessage = validateRegistrationForm(req);
            if (validationMessage != null) {
                handleError(req, resp, validationMessage);
                return;
            }

            String username = req.getParameter("username");
            String email = req.getParameter("email");

            // Check if username already exists
            if (registerService.usernameExists(username)) {
                handleError(req, resp, "Username already exists. Please choose a different username.");
                return;
            }

            // Check if email already exists
            if (registerService.emailExists(email)) {
                handleError(req, resp, "Email already exists. Please use a different email.");
                return;
            }

            // Extract user data from the form including image upload and encrypt password
            UsersModel user = extractUserModel(req);

            // Attempt to register the user
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

    /**
     * Validates the registration form inputs and the uploaded image.
     * Returns null if all is valid, otherwise returns an error message string.
     */
    private String validateRegistrationForm(HttpServletRequest req) {
        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String email = req.getParameter("email");
        String address = req.getParameter("address");
        String gender = req.getParameter("gender");
        String role = req.getParameter("role");

        // Basic non-null checks
        if (ValidationUtil.isNullOrEmpty(username)) return "Username is required.";
        if (ValidationUtil.isNullOrEmpty(password)) return "Password is required.";
        if (ValidationUtil.isNullOrEmpty(email)) return "Email is required.";
        if (ValidationUtil.isNullOrEmpty(address)) return "Address is required.";
        if (ValidationUtil.isNullOrEmpty(gender)) return "Gender is required.";
        if (ValidationUtil.isNullOrEmpty(role)) return "Role is required.";

        // Validate input formats
        if (!ValidationUtil.isValidUsername(username))
            return "Username must start with a letter and contain 5-20 alphanumeric characters.";
        if (!ValidationUtil.isValidPassword(password))
            return "Password must have at least 8 chars, with 1 capital letter, 1 number, 1 special character (@$!%*?&).";
        if (!ValidationUtil.isValidEmail(email))
            return "Invalid email format.";
        if (!ValidationUtil.isValidAddress(address))
            return "Address should be at least 5 characters long.";
        if (!ValidationUtil.isValidGender(gender))
            return "Gender must be one of: male, female, other.";
        if (!ValidationUtil.isValidRole(role))
            return "Role must be one of: Instructor, Student, Parent, Admin";

        // Validate uploaded image file extension
        try {
            Part image = req.getPart("imageUrl");
            if (!ValidationUtil.isValidImageExtension(image)) {
                return "Invalid image format. Only jpg, jpeg, png, and gif are allowed.";
            }
        } catch (IOException | ServletException e) {
            return "Error handling image file. Please ensure the file is valid.";
        }

        return null; // All validations passed
    }

    /**
     * Extracts user details from the request, handles image upload,
     * encrypts password, and sets role.
     */
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
        String rootPath = "C:\\Users\\Siddhi\\eclipse-workspace\\Moove\\src\\main\\webapp\\resources\\images\\";
        String uploadSubdirectory = "user";
        boolean uploadSuccess = imageUtil.uploadImage(image, rootPath, uploadSubdirectory);

        System.out.println("Image Name: " + imageName);
        System.out.println("Upload Subdirectory: " + uploadSubdirectory);
        System.out.println("Image upload success: " + uploadSuccess);

        // Set user data
        user.setUser_Name(username);
        user.setUser_Email(email);
        user.setUser_Status("active");
        user.setUser_Address(address);
        user.setUser_Gender(gender);
        user.setImage_path(imageName);

        // Encrypt the password before setting it
        String encryptedPassword = PasswordUtil.encrypt(password);
        if (encryptedPassword == null) {
            throw new RuntimeException("Password encryption failed");
        }
        user.setPassword(encryptedPassword);

        // Set role id based on role name
        int roleId = registerService.getRoleIdByName(role);
        user.setRole_Id(roleId);

        return user;
    }

    /**
     * Handles errors by setting attributes and forwarding back to the registration JSP page.
     */
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
