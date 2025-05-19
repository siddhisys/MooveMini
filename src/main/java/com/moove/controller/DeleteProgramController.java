package com.moove.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.moove.config.DbConfig;

// Servlet mapped to handle POST requests for deleting a program by name
@WebServlet("/DeleteProgram")
public class DeleteProgramController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Handles POST requests to delete a program
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Set response content type to JSON
        response.setContentType("application/json");

        // Get PrintWriter to send response back to client
        PrintWriter out = response.getWriter();

        // Initialize success flag and message for JSON response
        boolean success = false;
        String message = "";

        try {
            // Get program name parameter from the request
            String name = request.getParameter("name");

            // Validate that the program name is not null or empty
            if (name == null || name.trim().isEmpty()) {
                // Throw an exception if program name is missing
                throw new IllegalArgumentException("Program name is required");
            }

            // Get database connection from DbConfig
            Connection conn = DbConfig.getDbConnection();

            // Prepare SQL DELETE statement to remove program by name
            String sql = "DELETE FROM program WHERE Program_Name = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            // Set the program name parameter in the SQL statement
            pstmt.setString(1, name);

            // Execute the DELETE operation and get the number of rows affected
            int rows = pstmt.executeUpdate();

            // Determine if deletion was successful (at least one row deleted)
            success = rows > 0;

            // Set success or failure message accordingly
            message = success ? "Program deleted successfully" : "No program found with name: " + name;

            // Close PreparedStatement and Connection to free resources
            pstmt.close();
            conn.close();
        } catch (Exception e) {
            // If any exception occurs, mark success as false and set error message
            success = false;
            message = "Error: " + e.getMessage();

            // Print stack trace for debugging (can be logged in production)
            e.printStackTrace();
        }

        // Build JSON response string with success status and message
        String json = "{"
                + "\"success\":" + success + ","
                + "\"message\":\"" + escapeJson(message) + "\""
                + "}";

        // Send JSON response to client
        out.print(json);
        out.flush();
    }

    // Utility method to escape backslashes and double quotes in JSON strings
    private String escapeJson(String s) {
        return (s != null) ? s.replace("\\", "\\\\").replace("\"", "\\\"") : "";
    }
}
