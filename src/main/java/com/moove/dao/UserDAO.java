package com.moove.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

public class UserDAO {
    private Connection conn;

    public UserDAO(Connection conn) {
        this.conn = conn;
    }

    public int countUsersByRole(String roleName) throws SQLException {
        // Join users with roles table to match Role_Name
        String sql = "SELECT COUNT(*) FROM users u JOIN roles r ON u.Role_ID = r.Role_ID WHERE LOWER(r.Role_Name) = LOWER(?)";
        System.out.println("Executing countUsersByRole: " + sql + " with role: " + roleName);
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, roleName);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    System.out.println("Count for role " + roleName + ": " + count);
                    return count;
                }
                System.out.println("No data found for role " + roleName);
                return 0;
            }
        } catch (SQLException e) {
            System.err.println("SQL Error in countUsersByRole: " + e.getMessage());
            throw e;
        }
    }
    
    

    public Map<String, Integer> countUserStatus() throws SQLException {
        Map<String, Integer> statusMap = new HashMap<>();
        // Use User_Status as per schema
        String sql = "SELECT LOWER(User_Status) AS status, COUNT(*) as total FROM users GROUP BY LOWER(User_Status)";
        System.out.println("Executing countUserStatus: " + sql);
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                String status = rs.getString("status");
                int total = rs.getInt("total");
                statusMap.put(status != null ? status : "unknown", total);
                System.out.println("Status " + status + ": " + total);
            }
            if (statusMap.isEmpty()) System.out.println("No status data found in users table");
        } catch (SQLException e) {
            System.err.println("SQL Error in countUserStatus: " + e.getMessage());
            throw e;
        }
        return statusMap;
    }
}