package com.moove.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.moove.config.DbConfig;
import com.moove.model.ProgramModel;

/**
 * Servlet implementation class ListProgramsController
 * 
 * Handles HTTP GET requests to fetch and list all programs from the database.
 * Sets the list of programs as a request attribute and forwards to the admin JSP page.
 */
@WebServlet("/ListPrograms")
public class ListProgramsController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * Handles HTTP GET request to retrieve all programs ordered by program name.
     * 
     * @param request  HttpServletRequest containing client request data
     * @param response HttpServletResponse used to forward to JSP
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        // Initialize a list to hold ProgramModel objects fetched from the database
        List<ProgramModel> programs = new ArrayList<>();

        try {
            // Get a connection to the database from DbConfig utility class
            Connection conn = DbConfig.getDbConnection();

            // SQL query to select all programs ordered alphabetically by Program_Name
            String sql = "SELECT * FROM program ORDER BY Program_Name";

            // Prepare statement to avoid SQL injection
            PreparedStatement pstmt = conn.prepareStatement(sql);

            // Execute query and get result set
            ResultSet rs = pstmt.executeQuery();

            // Iterate through the result set to populate the list of ProgramModel
            while (rs.next()) {
                ProgramModel program = new ProgramModel();
                program.setProgram_Id(rs.getInt("Program_Id"));          // Set program ID
                program.setProgram_Name(rs.getString("Program_Name"));   // Set program name
                program.setProgram_Level(rs.getString("Program_Level")); // Set program level
                program.setProgram_Classes(rs.getInt("Program_Classes")); // Set number of classes
                program.setProgram_Desc(rs.getString("Program_Desc"));   // Set program description
                program.setImage_path(rs.getString("image_path"));       // Set image path
                
                // Add the program object to the list
                programs.add(program);
            }

            // Close JDBC resources to prevent resource leaks
            rs.close();
            pstmt.close();
            conn.close();
        } catch (Exception e) {
            // Log any exceptions that occur during database operations
            e.printStackTrace();
        }

        // Attach the list of programs as a request attribute for the JSP page
        request.setAttribute("programs", programs);

        // Forward the request to the AdminProgram.jsp page to display the program list
        request.getRequestDispatcher("/WEB-INF/pages/AdminProgram.jsp").forward(request, response);
    }
}
