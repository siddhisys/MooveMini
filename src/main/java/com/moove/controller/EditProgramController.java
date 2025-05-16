package com.moove.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.moove.config.DbConfig;

@WebServlet("/EditProgram")
public class EditProgramController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        boolean success = false;
        String message = "";

        Connection conn = null;
        PreparedStatement checkStmt = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // Parse parameters
            String name = request.getParameter("programName");
            String level = request.getParameter("programLevel");
            String classesParam = request.getParameter("programClasses");
            String desc = request.getParameter("programDesc");

            // Log parameters for debugging
            System.out.println("EditProgramController - Parameters received:");
            System.out.println("programName: " + name);
            System.out.println("programLevel: " + level);
            System.out.println("programClasses: " + classesParam);
            System.out.println("programDesc: " + desc);

            // Validate programName
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Program name is required");
            }
            
            name = name.trim(); // Ensure no leading/trailing whitespace

            // Establish database connection
            conn = DbConfig.getDbConnection();

            // Check if the program exists
            String checkSql = "SELECT Program_Level, Program_Classes, Program_Desc FROM program WHERE Program_Name = ?";
            checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, name);
            rs = checkStmt.executeQuery();

            if (!rs.next()) {
                message = "No program found with the name: " + name;
                String json = "{\"success\":false,\"message\":\"" + escapeJson(message) + "\"}";
                out.print(json);
                out.flush();
                return;
            }

            // Retrieve current values to compare for updates
            String currentLevel = rs.getString("Program_Level");
            int currentClasses = rs.getInt("Program_Classes");
            String currentDesc = rs.getString("Program_Desc");

            // Build dynamic SQL query for fields that are provided and have changed
            List<String> setClauses = new ArrayList<>();
            List<Object> params = new ArrayList<>();

            // Check if level is provided and different from current
            if (level != null && !level.trim().isEmpty()) {
                setClauses.add("Program_Level = ?");
                params.add(level.trim());
            }
            
            // Check if classes is provided and valid
            if (classesParam != null && !classesParam.trim().isEmpty()) {
                try {
                    int classes = Integer.parseInt(classesParam.trim());
                    setClauses.add("Program_Classes = ?");
                    params.add(classes);
                } catch (NumberFormatException e) {
                    throw new IllegalArgumentException("Invalid number format for classes: " + classesParam);
                }
            }
            
            // Check if description is provided
            if (desc != null) {
                setClauses.add("Program_Desc = ?");
                params.add(desc.trim());
            }

            // If no fields to update, return success message
            if (setClauses.isEmpty()) {
                success = true;
                message = "No changes detected for program: " + name;
                String json = "{\"success\":true,\"message\":\"" + escapeJson(message) + "\"}";
                out.print(json);
                out.flush();
                return;
            }

            // Construct the SQL query
            String sql = "UPDATE program SET " + String.join(", ", setClauses) + " WHERE Program_Name = ?";
            System.out.println("SQL Query: " + sql);
            params.add(name); // Add programName for WHERE clause

            pstmt = conn.prepareStatement(sql);

            // Set parameters dynamically
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                System.out.println("Parameter " + (i+1) + ": " + param);
                pstmt.setObject(i + 1, param);
            }

            int rows = pstmt.executeUpdate();
            System.out.println("Rows affected: " + rows);

            success = rows > 0;
            message = success ? "Program updated successfully" : "No update performed";

        } catch (IllegalArgumentException e) {
            success = false;
            message = e.getMessage();
            System.err.println("Validation error: " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            success = false;
            message = "Database error: " + e.getMessage();
            System.err.println("SQL error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            success = false;
            message = "Error: " + e.getMessage();
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (checkStmt != null) checkStmt.close();
                if (pstmt != null) pstmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                System.err.println("Error closing resources: " + e.getMessage());
                e.printStackTrace();
            }
        }

        // Construct JSON response
        String json = "{"
                + "\"success\":" + success + ","
                + "\"message\":\"" + escapeJson(message) + "\""
                + "}";
        System.out.println("Response: " + json);
        out.print(json);
        out.flush();
    }

    private String escapeJson(String s) {
        return (s != null) ? s.replace("\\", "\\\\").replace("\"", "\\\"") : "";
    }
}