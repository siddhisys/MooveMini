package com.moove.util;

import java.util.regex.Pattern;

public class ValidationUtil {

    // 1. Check if a field is null or empty
    public static boolean isNullOrEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    // 2. Username: Must contain only letters and spaces (no numbers, no special characters)
    public static boolean isValidUsername(String username) {
        return username != null && Pattern.matches("^[a-zA-Z][a-zA-Z ]{4,19}$", username); // 5-20 chars
    }

    // 3. Password: Cannot be empty
    public static boolean isValidPassword(String password) {
        return password != null && !password.trim().isEmpty();
    }


    // 4. Email: Standard email format
    public static boolean isValidEmail(String email) {
        String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        return email != null && Pattern.matches(emailRegex, email);
    }

    // 5. Address: At least 5 characters, can include letters, digits, spaces, commas, dots, and hyphens
    public static boolean isValidAddress(String address) {
        return address != null && address.length() >= 5 && address.matches("^[a-zA-Z0-9 ,.-]+$");
    }

    // 6. Gender: Must be one of the predefined options
    public static boolean isValidGender(String gender) {
        return gender != null && (
                gender.equalsIgnoreCase("male") ||
                gender.equalsIgnoreCase("female") ||
                gender.equalsIgnoreCase("other")
        );
    }

    // 7. Role: Must be one of the allowed roles
    public static boolean isValidRole(String role) {
        return role != null && (
                role.equalsIgnoreCase("Instructor") ||
                role.equalsIgnoreCase("Student") ||
                role.equalsIgnoreCase("Parent") ||
                role.equalsIgnoreCase("Admin")
        );
    }

    // Utility: Validate all register inputs at once (optional)
    public static boolean validateRegisterForm(String username, String password, String email,
                                              String address, String gender, String role) {
        return isValidUsername(username) &&
               isValidPassword(password) &&
               isValidEmail(email) &&
               isValidAddress(address) &&
               isValidGender(gender) &&
               isValidRole(role);
    }

	// 8. Image file extension validation
    public static boolean isValidImageExtension(jakarta.servlet.http.Part imagePart) {
        if (imagePart == null || imagePart.getSubmittedFileName() == null) {
            return false;
        }
        String fileName = imagePart.getSubmittedFileName().toLowerCase();
        return fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") || fileName.endsWith(".png") || fileName.endsWith(".gif");
    }
}