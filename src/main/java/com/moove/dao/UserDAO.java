package com.moove.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**
 * Data Access Object (DAO) for handling operations related to users in the database.
 */
public class UserDAO {
    private Connection conn;

    /**
     * Constructor to initialize the UserDAO with a database connection.
     * 
     * @param conn The active database connection.
     */
    public UserDAO(Connection conn) {
        this.conn = conn;
    }

    /**
     * Counts the number of users that belong to a specific role.
     * 
     * This method joins the `users` table with the `roles` table on Role_ID
     * and filters the results by matching the role name (case-insensitive).
     * 
     * @param roleName The name of the role (e.g., "admin", "trainer").
     * @return The number of users having the specified role.
     * @throws SQLException If a database access error occurs.
     */
    public int countUsersByRole(String roleName) throws SQLException {
        // SQL query to count users based on role name (case-insensitive match)
        String sql = "SELECT COUNT(*) FROM users u JOIN roles r ON u.Role_ID = r.Role_ID WHERE LOWER(r.Role_Name) = LOWER(?)";
        System.out.println("Executing countUsersByRole: " + sql + " with role: " + roleName);

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Set the role name parameter
            stmt.setString(1, roleName);

            // Execute query and get result
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    System.out.println("Count for role " + roleName + ": " + count);
                    return count;
                }

                // No results found
                System.out.println("No data found for role " + roleName);
                return 0;
            }
        } catch (SQLException e) {
            // Log error and re-throw
            System.err.println("SQL Error in countUsersByRole: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Counts users grouped by their status (e.g., active, inactive, pending).
     * 
     * This method queries the `users` table and groups them by the `User_Status` column.
     * It converts all statuses to lowercase for consistency.
     * 
     * @return A map where the key is the user status and the value is the count.
     * @throws SQLException If a database access error occurs.
     */
    public Map<String, Integer> countUserStatus() throws SQLException {
        Map<String, Integer> statusMap = new HashMap<>();

        // SQL query to count users by their status
        String sql = "SELECT LOWER(User_Status) AS status, COUNT(*) as total FROM users GROUP BY LOWER(User_Status)";
        System.out.println("Executing countUserStatus: " + sql);

        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            // Loop through the result set and populate the map
            while (rs.next()) {
                String status = rs.getString("status");
                int total = rs.getInt("total");

                // Put the status into the map, using "unknown" if status is null
                statusMap.put(status != null ? status : "unknown", total);
                System.out.println("Status " + status + ": " + total);
            }

            // Check if no data was found
            if (statusMap.isEmpty()) {
                System.out.println("No status data found in users table");
            }
        } catch (SQLException e) {
            // Log error and re-throw
            System.err.println("SQL Error in countUserStatus: " + e.getMessage());
            throw e;
        }

        return statusMap;
    }
}
