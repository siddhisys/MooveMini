package com.moove.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.moove.config.DbConfig;
import com.moove.model.UsersModel;
import com.moove.util.PasswordUtil;

/**
 * Service class for handling user registration operations in the Moove application.
 */
public class RegisterService {
    private Connection dbConn;
    private boolean isConnectionError = false;

    /**
     * Constructor initializes the database connection.
     */
    public RegisterService() {
        try {
            dbConn = DbConfig.getDbConnection();
        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
            isConnectionError = true;
        }
    }

    /**
     * Registers a new user in the database.
     *
     * @param userModel the UserModel object containing user information
     * @return true if registration is successful, false otherwise
     */
    public boolean registerUser(UsersModel userModel) {
        if (isConnectionError) {
            System.out.println("Database connection error during user registration!");
            return false;
        }
        
        // Password is already encrypted in the Controller, don't encrypt again
        
        String query = "INSERT INTO users (User_Name, User_Email, password, User_Address, User_Status, User_Gender, image_path, Role_Id) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = dbConn.prepareStatement(query)) {
            stmt.setString(1, userModel.getUser_Name());
            stmt.setString(2, userModel.getUser_Email());
            stmt.setString(3, userModel.getPassword());
            stmt.setString(4, userModel.getUser_Address());
            stmt.setString(5, userModel.getUser_Status());
            stmt.setString(6, userModel.getUser_Gender());
            stmt.setString(7, userModel.getImage_path());
            stmt.setInt(8, userModel.getRole_Id());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates an existing user's profile information in the database.
     *
     * @param userModel the UsersModel object containing updated user information
     * @return true if the update is successful, false otherwise
     */
    public boolean updateUserProfile(UsersModel userModel) {
        if (isConnectionError) {
            System.out.println("Database connection error during profile update!");
            return false;
        }

        // Encrypt the password before storing
        String encryptedPassword = PasswordUtil.encrypt(userModel.getPassword(), userModel.getUser_Name());
        
        StringBuilder queryBuilder = new StringBuilder("UPDATE users SET User_Email = ?, password = ?, User_Address = ?");
        
        // Only include image_path in the query if it's not null
        if (userModel.getImage_path() != null && !userModel.getImage_path().isEmpty()) {
            queryBuilder.append(", image_path = ?");
        }
        
        queryBuilder.append(" WHERE User_Name = ?");
        
        try (PreparedStatement stmt = dbConn.prepareStatement(queryBuilder.toString())) {
            stmt.setString(1, userModel.getUser_Email());
            stmt.setString(2, encryptedPassword);
            stmt.setString(3, userModel.getUser_Address());
            
            int paramIndex = 4;
            
            // Set image_path parameter if it exists
            if (userModel.getImage_path() != null && !userModel.getImage_path().isEmpty()) {
                stmt.setString(paramIndex++, userModel.getImage_path());
            }
            
            stmt.setString(paramIndex, userModel.getUser_Name());
            
            int rowsAffected = stmt.executeUpdate();
            
            System.out.println("Profile update - Rows affected: " + rowsAffected);
            System.out.println("Profile update - SQL: " + queryBuilder.toString());
            
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.out.println("Error updating profile: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Checks if a username already exists in the database.
     *
     * @param username the username to check
     * @return true if the username exists, false otherwise
     */
    public boolean usernameExists(String username) {
        if (isConnectionError) {
            return false;
        }

        String query = "SELECT User_Name FROM users WHERE User_Name = ?";
        try (PreparedStatement stmt = dbConn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet result = stmt.executeQuery();

            return result.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Checks if an email already exists in the database.
     *
     * @param email the email to check
     * @return true if the email exists, false otherwise
     */
    public boolean emailExists(String email) {
        if (isConnectionError) {
            return false;
        }

        String query = "SELECT User_Email FROM users WHERE User_Email = ?";
        try (PreparedStatement stmt = dbConn.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet result = stmt.executeQuery();

            return result.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Gets the role ID associated with the given role name from the database.
     * 
     * This method first queries the database for the role ID. If the role is not found
     * in the database, it falls back to default IDs based on common role names.
     *
     * @param roleName The name of the role (Instructor, Student, Parent, Admin)
     * @return The role ID from the database, or a default value if not found
     */
    public int getRoleIdByName(String roleName) {
        if (isConnectionError) {
            System.out.println("Database connection error while getting role ID!");
            return 0; // Return default role ID in case of connection error
        }

        String query = "SELECT Role_Id FROM roles WHERE Role_Name = ?";
        try (PreparedStatement stmt = dbConn.prepareStatement(query)) {
            stmt.setString(1, roleName);
            ResultSet result = stmt.executeQuery();

            if (result.next()) {
                return result.getInt("Role_Id");
            } else {
                System.out.println("Role not found: " + roleName);
                // Return default role ID if role not found
                // You might want to define constants for your role IDs
                switch (roleName.toLowerCase()) {
                    case "admin":
                        return 1;
                    case "instructor":
                        return 2;
                    case "student":
                        return 3;
                    case "parent":
                        return 4;
                    default:
                        return 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving role ID: " + e.getMessage());
            e.printStackTrace();
            return 0; // Return default role ID in case of SQL error
        }
    }
}