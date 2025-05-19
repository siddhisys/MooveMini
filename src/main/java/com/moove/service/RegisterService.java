package com.moove.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.moove.config.DbConfig;
import com.moove.model.UsersModel;

/**
 * Service class for handling user registration and profile-related operations in the Moove application.
 */
public class RegisterService {
    private Connection dbConn;
    private boolean isConnectionError = false;

    /**
     * Constructor initializes the database connection from DbConfig.
     * If an error occurs, sets isConnectionError flag to true.
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
     * Registers a new user in the 'users' table.
     * Assumes password is already encrypted before passing this method.
     *
     * @param userModel the UsersModel object containing the user's data
     * @return true if insertion is successful; false otherwise
     */
    public boolean registerUser(UsersModel userModel) {
        if (isConnectionError) {
            System.out.println("Database connection error during user registration!");
            return false;
        }

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
     * Updates a user's profile information in the database.
     * Conditionally updates image_path if it's provided.
     *
     * @param userModel the UsersModel object with updated information
     * @return true if update was successful; false otherwise
     */
    public boolean updateUserProfile(UsersModel userModel) {
        if (isConnectionError) {
            System.out.println("Database connection error during profile update!");
            return false;
        }

        // Build SQL update query
        StringBuilder queryBuilder = new StringBuilder("UPDATE users SET User_Name = ?, User_Email = ?, password = ?, User_Address = ?");
        if (userModel.getImage_path() != null && !userModel.getImage_path().isEmpty()) {
            queryBuilder.append(", image_path = ?");
        }
        queryBuilder.append(" WHERE User_ID = ?");

        try (PreparedStatement stmt = dbConn.prepareStatement(queryBuilder.toString())) {
            stmt.setString(1, userModel.getUser_Name());
            stmt.setString(2, userModel.getUser_Email());
            stmt.setString(3, userModel.getPassword());
            stmt.setString(4, userModel.getUser_Address());

            int paramIndex = 5;

            // Conditionally add image_path
            if (userModel.getImage_path() != null && !userModel.getImage_path().isEmpty()) {
                stmt.setString(paramIndex++, userModel.getImage_path());
            }

            stmt.setInt(paramIndex, userModel.getUser_ID());

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
     * Checks whether a given username already exists in the database.
     *
     * @param username the username to check
     * @return true if username exists; false otherwise
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
     * Checks whether a given email address is already registered.
     *
     * @param email the email address to check
     * @return true if email exists; false otherwise
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
     * Retrieves the Role ID based on a role name from the 'roles' table.
     * Falls back to hardcoded default values if the role is not found.
     *
     * @param roleName the name of the role (e.g., "admin", "student")
     * @return the role ID; returns 0 if role not found or error occurs
     */
    public int getRoleIdByName(String roleName) {
        if (isConnectionError) {
            System.out.println("Database connection error while getting role ID!");
            return 0;
        }

        String query = "SELECT Role_Id FROM roles WHERE Role_Name = ?";
        try (PreparedStatement stmt = dbConn.prepareStatement(query)) {
            stmt.setString(1, roleName);
            ResultSet result = stmt.executeQuery();

            if (result.next()) {
                return result.getInt("Role_Id");
            } else {
                System.out.println("Role not found: " + roleName);
                // Fallback to default hardcoded role IDs
                switch (roleName.toLowerCase()) {
                    case "admin": return 1;
                    case "instructor": return 2;
                    case "student": return 3;
                    case "parent": return 4;
                    default: return 0;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving role ID: " + e.getMessage());
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Deletes a user from the database based on their user ID.
     * Ensures transactional safety by using commit and rollback.
     *
     * @param userId the ID of the user to delete
     * @return true if the user was successfully deleted; false otherwise
     */
    public boolean deleteUser(int userId) {
        if (isConnectionError) {
            System.out.println("Database connection error during user deletion!");
            return false;
        }

        try {
            System.out.println("Setting autoCommit to false");
            dbConn.setAutoCommit(false);

            // Delete user entry
            String deleteUserQuery = "DELETE FROM users WHERE User_ID = ?";
            try (PreparedStatement stmt = dbConn.prepareStatement(deleteUserQuery)) {
                stmt.setInt(1, userId);
                int rowsAffected = stmt.executeUpdate();
                System.out.println("User deletion - Rows affected: " + rowsAffected);

                if (rowsAffected > 0) {
                    dbConn.commit();
                    return true;
                } else {
                    dbConn.rollback();
                    return false;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error deleting user: " + e.getMessage());
            e.printStackTrace();
            try {
                dbConn.rollback();
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
            return false;
        } finally {
            try {
                dbConn.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
