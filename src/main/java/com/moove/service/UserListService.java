package com.moove.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.moove.config.DbConfig;
import com.moove.model.UsersModel;
import jakarta.servlet.ServletException;

public class UserListService {
    private Connection connection;
    
    public UserListService() throws ServletException {
        try {
            this.connection = DbConfig.getDbConnection();
            if (this.connection == null) {
                throw new ServletException("Connection is null - check DbConfig");
            }
            if (this.connection.isClosed()) {
                throw new ServletException("Connection is closed immediately after creation");
            }
            System.out.println("Database connection established successfully");
        } catch (SQLException | ClassNotFoundException ex) {
            System.err.println("Database connection failed:");
            ex.printStackTrace();
            throw new ServletException("Database connection error", ex);
        }
    }
    
    
    public List<UsersModel> getAllUsers() throws SQLException {
        List<UsersModel> users = new ArrayList<>();
        String query = "SELECT User_ID, User_Name, User_Email, User_Gender, User_Status, Role_Id FROM users";
        
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet result = stmt.executeQuery()) {
            
            while (result.next()) {
                users.add(extractUserFromResultSet(result));
            }
        }
        return users;
    }
    
    private UsersModel extractUserFromResultSet(ResultSet result) throws SQLException {
        UsersModel user = new UsersModel();
        user.setUser_ID(result.getInt("User_ID"));
        user.setUser_Name(result.getString("User_Name"));
        user.setUser_Email(result.getString("User_Email"));
        user.setUser_Gender(result.getString("User_Gender"));
        user.setUser_Status(result.getString("User_Status"));
        user.setRole_Id(result.getInt("Role_Id"));
        return user;
    }
    
    public UsersModel getUserById(int userId) throws SQLException {
        String query = "SELECT User_ID, User_Name, User_Email, User_Gender, User_Status, Role_Id FROM users WHERE User_ID = ?";
        
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            
            try (ResultSet result = stmt.executeQuery()) {
                if (result.next()) {
                    return extractUserFromResultSet(result);
                }
            }
        }
        return null;
    }
    
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }
}