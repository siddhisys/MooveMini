package com.moove.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.moove.config.DbConfig;
import com.moove.model.UsersModel;
import jakarta.servlet.ServletException;

/**
 * Service class responsible for retrieving user-related data from the database,
 * including associated program information via enrollments.
 */
public class UserListService {
    private Connection connection;

    /**
     * Constructor that initializes a database connection.
     * 
     * @throws ServletException if the connection fails to establish
     */
    public UserListService() throws ServletException {
        try {
            this.connection = DbConfig.getDbConnection(); // Get DB connection from config
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

    /**
     * Retrieves a list of all users and the names of the programs they are enrolled in.
     * 
     * @return a map containing:
     *         - "users": List of UsersModel
     *         - "userProgramMap": Map of user IDs to comma-separated program names
     * @throws SQLException if an error occurs during database operations
     */
    public Map<String, Object> getAllUsersWithPrograms() throws SQLException {
        List<UsersModel> users = new ArrayList<>();
        Map<Integer, String> userProgramMap = new HashMap<>();

        // SQL joins users, enrollments, and programs, grouping by user
        String query = "SELECT u.User_ID, u.User_Name, u.User_Email, u.User_Gender, u.User_Status, u.Role_Id, " +
                       "COALESCE(GROUP_CONCAT(p.Program_Name SEPARATOR ', '), 'None') AS program_name " +
                       "FROM users u " +
                       "LEFT JOIN enrollments e ON u.User_ID = e.User_Id " +
                       "LEFT JOIN program p ON e.Program_Id = p.Program_Id " +
                       "GROUP BY u.User_ID, u.User_Name, u.User_Email, u.User_Gender, u.User_Status, u.Role_Id";

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet result = stmt.executeQuery()) {

            // Extract user info and associated programs
            while (result.next()) {
                UsersModel user = extractUserFromResultSet(result);
                users.add(user);
                userProgramMap.put(user.getUser_ID(), result.getString("program_name"));
            }
        }

        // Combine results into a map
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("users", users);
        resultMap.put("userProgramMap", userProgramMap);
        return resultMap;
    }

    /**
     * Helper method to extract user details from a result set.
     * 
     * @param result the ResultSet containing user info
     * @return a UsersModel object populated with the data
     * @throws SQLException if an error occurs while reading the result set
     */
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

    /**
     * Retrieves a specific user and their enrolled program using their ID.
     * 
     * @param userId the ID of the user to fetch
     * @return a map containing:
     *         - "user": UsersModel
     *         - "userProgramMap": Map of user ID to program name
     * @throws SQLException if an error occurs during the database query
     */
    public Map<String, Object> getUserByIdWithProgram(int userId) throws SQLException {
        Map<Integer, String> userProgramMap = new HashMap<>();
        UsersModel user = null;

        String query = "SELECT u.User_ID, u.User_Name, u.User_Email, u.User_Gender, u.User_Status, u.Role_Id, " +
                       "COALESCE(p.Program_Name, 'None') AS program_name " +
                       "FROM users u " +
                       "LEFT JOIN enrollments e ON u.User_ID = e.User_Id " +
                       "LEFT JOIN program p ON e.Program_Id = p.Program_Id " +
                       "WHERE u.User_ID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId); // Set parameter to the passed userId

            try (ResultSet result = stmt.executeQuery()) {
                if (result.next()) {
                    user = extractUserFromResultSet(result);
                    userProgramMap.put(user.getUser_ID(), result.getString("program_name"));
                }
            }
        }

        // Combine user and program info into a map
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("user", user);
        resultMap.put("userProgramMap", userProgramMap);
        return resultMap;
    }

    /**
     * Closes the database connection if it is still open.
     */
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
