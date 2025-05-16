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
 * Connects to the database, verifies user credentials, and returns login status.
 */
public class LoginService {
    private Connection dbConn;
    private boolean isConnectionError = false;

    public LoginService() {
        try {
            dbConn = DbConfig.getDbConnection();
        } catch (SQLException | ClassNotFoundException ex) {
            ex.printStackTrace();
            isConnectionError = true;
        }
    }

    public Boolean authenticateUser(UsersModel userModel) {
        if (isConnectionError) {
            System.out.println("Database connection error!");
            return null;
        }

        String query = "SELECT User_Name, password FROM users WHERE User_Name = ?";
        try (PreparedStatement stmt = dbConn.prepareStatement(query)) {
            stmt.setString(1, userModel.getUser_Name());
            ResultSet result = stmt.executeQuery();

            if (result.next()) {
                return validatePassword(result, userModel);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }

        return false;
    }

    public UsersModel getUserByUsername(String username) {
        if (isConnectionError) {
            System.out.println("Database connection error!");
            return null;
        }

        String query = "SELECT User_ID, User_Name, User_Email, password, User_Address, image_path FROM users WHERE User_Name = ?";
        try (PreparedStatement stmt = dbConn.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet result = stmt.executeQuery();

            if (result.next()) {
                UsersModel user = new UsersModel();
                user.setUser_ID(result.getInt("User_ID"));
                user.setUser_Name(result.getString("User_Name"));
                user.setUser_Email(result.getString("User_Email"));
                user.setPassword(result.getString("password"));
                user.setUser_Address(result.getString("User_Address"));
                user.setImage_path(result.getString("image_path")); // Ensure image_path is retrieved
                System.out.println("Retrieved image_path from DB: " + user.getImage_path());
                return user;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Error retrieving user by username: " + e.getMessage());
        }

        return null;
    }

    private boolean validatePassword(ResultSet result, UsersModel userModel) throws SQLException {
        String dbUsername = result.getString("User_Name");
        String dbPassword = result.getString("password");

        String decryptedPassword = PasswordUtil.decrypt(dbPassword, dbUsername);

        return dbUsername.equals(userModel.getUser_Name())
            && decryptedPassword != null
            && decryptedPassword.equals(userModel.getPassword());
    }

    public boolean userExists(String User_Name) {
        if (isConnectionError) {
            return false;
        }

        String query = "SELECT User_Name FROM users WHERE User_Name = ?";
        try (PreparedStatement stmt = dbConn.prepareStatement(query)) {
            stmt.setString(1, User_Name);
            ResultSet result = stmt.executeQuery();

            return result.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}