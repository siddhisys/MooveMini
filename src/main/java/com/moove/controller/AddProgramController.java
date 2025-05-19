package com.moove.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.UUID;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import com.moove.config.DbConfig;

// Servlet annotation to map URL pattern "/AddProgram"
@WebServlet("/AddProgram")
// Configuration for handling file uploads (multipart/form-data)
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,       // 1 MB threshold for writing files to disk
    maxFileSize = 1024 * 1024 * 10,        // Max file size 10 MB
    maxRequestSize = 1024 * 1024 * 30      // Max request size 30 MB
)
public class AddProgramController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    // Handles POST requests to add a new program
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Set response content type to JSON
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        boolean success = false;    // Flag to track if operation succeeded
        String message = "";        // Message to send back in response

        try {
            // Retrieve form parameters from the request
            String name = request.getParameter("programName");
            String level = request.getParameter("programLevel");
            int classes = Integer.parseInt(request.getParameter("programClasses")); // Convert classes count to int
            String desc = request.getParameter("programDesc");

            // Get the uploaded file part (image)
            Part filePart = request.getPart("imagePath");

            // Generate a unique file name using UUID to avoid collisions, preserving original extension
            String fileName = UUID.randomUUID().toString() + "." + getFileExtension(filePart);

            // Define the upload directory path inside the web application folder
            String uploadPath = getServletContext().getRealPath("") 
                    + File.separator + "resources" 
                    + File.separator + "images" 
                    + File.separator + "programs";

            // Create upload directory if it doesn't exist
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) uploadDir.mkdirs();

            // Save the uploaded file to the specified location
            filePart.write(uploadPath + File.separator + fileName);

            // Get a database connection from the configuration class
            Connection conn = DbConfig.getDbConnection();

            // Prepare SQL insert statement to add program details to the database
            String sql = "INSERT INTO program (Program_Name, Program_Level, Program_Classes, Program_Desc, image_path) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, level);
            pstmt.setInt(3, classes);
            pstmt.setString(4, desc);
            pstmt.setString(5, fileName);

            // Execute the insert and check if at least one row was affected
            int rows = pstmt.executeUpdate();

            success = rows > 0;
            message = success ? "Program added successfully" : "Failed to add program";

            // Clean up database resources
            pstmt.close();
            conn.close();

        } catch (Exception e) {
            // If an exception occurs, mark success as false and set error message
            success = false;
            message = "Error: " + e.getMessage();
            e.printStackTrace();
        }

        // Construct JSON response with success status and message
        String json = "{"
                + "\"success\":" + success + ","
                + "\"message\":\"" + escapeJson(message) + "\""
                + "}";
        out.print(json);
        out.flush();
    }

    // Helper method to extract file extension from the uploaded file's content-disposition header
    private String getFileExtension(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        // Example: form-data; name="imagePath"; filename="example.jpg"
        String fileName = contentDisp.substring(contentDisp.lastIndexOf("=") + 2, contentDisp.length() - 1);
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    // Helper method to escape characters in JSON string to avoid syntax errors
    private String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
