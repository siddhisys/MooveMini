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

// Servlet mapped to handle POST requests for editing a program
@WebServlet("/EditProgram")
public class EditProgramController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Handles POST requests to update program details
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Set response content type to JSON
        response.setContentType("application/json");

        // Get writer to send JSON response back to client
        PrintWriter out = response.getWriter();

        // Variables to track success status and response message
        boolean success = false;
        String message = "";

        // Declare database resources outside try block for proper closing in finally
        Connection conn = null;
        PreparedStatement checkStmt = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // Parse input parameters from request
            String name = request.getParameter("programName");
            String level = request.getParameter("programLevel");
            String classesParam = request.getParameter("programClasses");
            String desc = request.getParameter("programDesc");

            // Log parameters for debugging purposes
            System.out.println("EditProgramController - Parameters received:");
            System.out.println("programName: " + name);
            System.out.println("programLevel: " + level);
            System.out.println("programClasses: " + classesParam);
            System.out.println("programDesc: " + desc);

            // Validate that programName is provided and not empty
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Program name is required");
            }
            name = name.trim(); // Remove leading/trailing whitespace

            // Establish database connection
            conn = DbConfig.getDbConnection();

            // Check if the program with the given name exists in the database
            String checkSql = "SELECT Program_Level, Program_Classes, Program_Desc FROM program WHERE Program_Name = ?";
            checkStmt = conn.prepareStatement(checkSql);
            checkStmt.setString(1, name);
            rs = checkStmt.executeQuery();

            // If program does not exist, respond with failure message and return early
            if (!rs.next()) {
                message = "No program found with the name: " + name;
                String json = "{\"success\":false,\"message\":\"" + escapeJson(message) + "\"}";
                out.print(json);
                out.flush();
                return;
            }

            // Prepare to build dynamic SQL UPDATE statement based on provided fields
            List<String> setClauses = new ArrayList<>();
            List<Object> params = new ArrayList<>();

            // If programLevel is provided and non-empty, add to update clause and parameters
            if (level != null && !level.trim().isEmpty()) {
                setClauses.add("Program_Level = ?");
                params.add(level.trim());
            }

            // If programClasses is provided and non-empty, try to parse to int and add to update
            if (classesParam != null && !classesParam.trim().isEmpty()) {
                try {
                    int classes = Integer.parseInt(classesParam.trim());
                    setClauses.add("Program_Classes = ?");
                    params.add(classes);
                } catch (NumberFormatException e) {
                    // Throw an exception if the classes parameter is invalid number
                    throw new IllegalArgumentException("Invalid number format for classes: " + classesParam);
                }
            }

            // If programDesc is provided (can be empty string), add to update clause
            if (desc != null) {
                setClauses.add("Program_Desc = ?");
                params.add(desc.trim());
            }

            // If no fields are provided for update, return success with "no changes" message
            if (setClauses.isEmpty()) {
                success = true;
                message = "No changes detected for program: " + name;
                String json = "{\"success\":true,\"message\":\"" + escapeJson(message) + "\"}";
                out.print(json);
                out.flush();
                return;
            }

            // Construct the UPDATE SQL query dynamically with the fields to be updated
            String sql = "UPDATE program SET " + String.join(", ", setClauses) + " WHERE Program_Name = ?";
            System.out.println("SQL Query: " + sql);

            // Add programName as last parameter for WHERE clause
            params.add(name);

            // Prepare the statement with the constructed SQL
            pstmt = conn.prepareStatement(sql);

            // Set all parameters dynamically in the prepared statement
            for (int i = 0; i < params.size(); i++) {
                Object param = params.get(i);
                System.out.println("Parameter " + (i + 1) + ": " + param);
                pstmt.setObject(i + 1, param);
            }

            // Execute the UPDATE query and get the number of affected rows
            int rows = pstmt.executeUpdate();
            System.out.println("Rows affected: " + rows);

            // Determine success based on whether rows were updated
            success = rows > 0;
            message = success ? "Program updated successfully" : "No update performed";

        } catch (IllegalArgumentException e) {
            // Handle validation errors
            success = false;
            message = e.getMessage();
            System.err.println("Validation error: " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            // Handle SQL/database errors
            success = false;
            message = "Database error: " + e.getMessage();
            System.err.println("SQL error: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            // Handle unexpected exceptions
            success = false;
            message = "Error: " + e.getMessage();
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // Close all database resources safely
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

        // Build JSON response string with success flag and message
        String json = "{"
                + "\"success\":" + success + ","
                + "\"message\":\"" + escapeJson(message) + "\""
                + "}";
        System.out.println("Response: " + json);

        // Send JSON response to client
        out.print(json);
        out.flush();
    }

    // Utility method to escape special characters in JSON strings (backslash and quotes)
    private String escapeJson(String s) {
        return (s != null) ? s.replace("\\", "\\\\").replace("\"", "\\\"") : "";
    }
}
