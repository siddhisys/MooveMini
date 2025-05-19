package com.moove.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import com.moove.config.DbConfig;

/**
 * Servlet implementation class GetProgramController
 * 
 * Handles HTTP GET requests to fetch program details by program name.
 * Responds with JSON data representing the program details if found.
 */
@WebServlet("/GetProgram")
public class GetProgramController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * Handles the HTTP GET request to fetch program details by "name" parameter.
     * 
     * @param request  HttpServletRequest containing client request data
     * @param response HttpServletResponse used to send JSON response
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Set response content type to JSON for client-side processing
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        // StringBuilder to construct the JSON response dynamically
        StringBuilder json = new StringBuilder();

        // Variables to track success and error message
        boolean success = false;
        String message = "";

        try {
            // Get 'name' parameter from the request to identify the program
            String name = request.getParameter("name");
            
            // Validate that the program name is provided
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Program name is required");
            }

            // Obtain a database connection from the configuration class
            Connection conn = DbConfig.getDbConnection();
            
            // Prepare SQL query to find the program by its name (case-sensitive exact match)
            String sql = "SELECT * FROM program WHERE Program_Name = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            
            // Execute the query
            ResultSet rs = pstmt.executeQuery();

            // If a program is found, extract details and build JSON response
            if (rs.next()) {
                success = true;
                
                // Print details to server console for debugging/logging
                System.out.println("GetProgramController - Program_Name: " + rs.getString("Program_Name"));
                System.out.println("GetProgramController - Program_Classes: " + rs.getInt("Program_Classes"));
                
                // Build JSON string manually with escaped values
                json.append("{")
                    .append("\"program_Name\":\"").append(escapeJson(rs.getString("Program_Name"))).append("\",")
                    .append("\"program_Level\":\"").append(escapeJson(rs.getString("Program_Level"))).append("\",")
                    .append("\"program_Classes\":").append(rs.getInt("Program_Classes")).append(",")
                    .append("\"program_Desc\":\"").append(escapeJson(rs.getString("Program_Desc"))).append("\",")
                    .append("\"image_path\":\"").append(escapeJson(rs.getString("image_path"))).append("\",")
                    .append("\"success\":true")
                    .append("}");
            } else {
                // No program found with the given name
                message = "Program not found with name: " + name;
            }

            // Clean up JDBC resources
            rs.close();
            pstmt.close();
            conn.close();
        } catch (Exception e) {
            // Capture and log any exceptions and prepare error message for JSON response
            message = "Error: " + e.getMessage();
            e.printStackTrace();
        }

        // If success is false (program not found or error occurred), send failure JSON
        if (!success) {
            json.append("{")
                .append("\"success\":false,")
                .append("\"message\":\"").append(escapeJson(message)).append("\"")
                .append("}");
        }

        // Write the JSON response to the client
        out.print(json.toString());
        out.flush();
    }

    /**
     * Escapes special characters in a string to safely include it in JSON.
     * Handles backslashes, double quotes, newline and carriage return characters.
     * 
     * @param s input string to escape
     * @return escaped string safe for JSON inclusion
     */
    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\")    // Escape backslash
                .replace("\"", "\\\"")    // Escape double quotes
                .replace("\n", "\\n")     // Escape newline
                .replace("\r", "\\r");    // Escape carriage return
    }
}
