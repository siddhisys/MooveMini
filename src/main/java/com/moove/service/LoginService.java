package com.moove.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import com.moove.config.DbConfig;
import com.moove.model.UsersModel;
import com.moove.util.PasswordUtil;

/**
 * Service class for handling login operations for the Moove application.
 * It manages user authentication, retrieval of user data, and verification of user existence.
 */
public class LoginService {
    private Connection dbConn;                // Database connection object
    private boolean isConnectionError = false; // Flag to check if a DB connection error occurred

    /**
     * Constructor attempts to establish a database connection using DbConfig.
     * If it fails, sets a flag to prevent further DB operations.
     */
    public LoginService() {
        try {
            dbConn = DbConfig.getDbConnection(); // Initialize DB connection
        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();                // Print stack trace for debugging
            isConnectionError = true;            // Set error flag
        }
    }

    /**
     * Authenticates a user by verifying username and password.
     * 
     * @param userModel User credentials provided during login
     * @return true if authentication is successful,
     *         false if password is incorrect,
     *         null if there's a DB error
     */
    public Boolean authenticateUser(UsersModel userModel) {
        if (isConnectionError) {
            System.out.println("Database connection error!");
            return null;
        }

        String query = "SELECT User_Name, password FROM users WHERE User_Name = ?";
        try (PreparedStatement stmt = dbConn.prepareStatement(query)) {
            stmt.setString(1, userModel.getUser_Name()); // Set username in query
            ResultSet result = stmt.executeQuery();

            // If user found, validate password
            if (result.next()) {
                return validatePassword(result, userModel);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Print SQL error
            return null;
        }

        return false; // User not found or invalid password
    }

    /**
     * Retrieves full user details by username.
     * 
     * @param username The username to search
     * @return UsersModel object if user exists, null otherwise
     */
    public UsersModel getUserByUsername(String username) {
        if (isConnectionError) {
            System.out.println("Database connection error!");
            return null;
        }

        String query = "SELECT User_ID, User_Name, User_Email, password, User_Address, image_path FROM users WHERE User_Name = ?";
        try (PreparedStatement stmt = dbConn.prepareStatement(query)) {
            stmt.setString(1, username); // Set username in query
            ResultSet result = stmt.executeQuery();

            if (result.next()) {
                // Populate user model with data from DB
                UsersModel user = new UsersModel();
                user.setUser_ID(result.getInt("User_ID"));
                user.setUser_Name(result.getString("User_Name"));
                user.setUser_Email(result.getString("User_Email"));
                user.setPassword(result.getString("password"));
                user.setUser_Address(result.getString("User_Address"));
                user.setImage_path(result.getString("image_path")); // Set image path
                System.out.println("Retrieved image_path from DB: " + user.getImage_path());
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error retrieving user by username: " + e.getMessage());
        }

        return null; // User not found
    }

    /**
     * Validates the password of the user by decrypting the stored password and comparing it.
     *
     * @param result ResultSet containing DB username and encrypted password
     * @param userModel User-provided credentials
     * @return true if passwords match, false otherwise
     * @throws SQLException if result parsing fails
     */
    private boolean validatePassword(ResultSet result, UsersModel userModel) throws SQLException {
        String dbUsername = result.getString("User_Name");
        String dbPassword = result.getString("password");

        try {
            // Decrypt the password stored in DB
            String decryptedPassword = PasswordUtil.decrypt(dbPassword);
            // Compare both username and password
            return dbUsername.equals(userModel.getUser_Name())
                    && decryptedPassword != null
                    && decryptedPassword.equals(userModel.getPassword());
        } catch (RuntimeException e) {
            System.out.println("Password decryption failed: " + e.getMessage());
            return false;
        }
    }

    /**
     * Checks whether a user exists in the database by username.
     *
     * @param User_Name Username to check
     * @return true if user exists, false otherwise
     */
    public boolean userExists(String User_Name) {
        if (isConnectionError) {
            return false;
        }

        String query = "SELECT User_Name FROM users WHERE User_Name = ?";
        try (PreparedStatement stmt = dbConn.prepareStatement(query)) {
            stmt.setString(1, User_Name); // Set username in query
            ResultSet result = stmt.executeQuery();

            return result.next(); // Returns true if user exists
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
