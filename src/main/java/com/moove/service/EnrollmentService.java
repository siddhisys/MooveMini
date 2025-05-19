package com.moove.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.moove.dao.EnrollmentDAO;
import com.moove.model.EnrollmentModel;
import com.moove.model.ProgramModel;

/**
 * Service class for handling enrollment-related operations such as
 * enrolling a student and retrieving a user's enrolled programs.
 */
public class EnrollmentService {

    // Database connection details
    private static final String jdbcURL = "jdbc:mysql://localhost:3306/moove";
    private static final String jdbcUsername = "root";
    private static final String jdbcPassword = "";

    /**
     * Enrolls a student in a program using the provided EnrollmentModel.
     *
     * @param enrollment The enrollment details (user ID, program ID, etc.)
     * @return true if enrollment is successful, false otherwise
     */
    public boolean enrollStudent(EnrollmentModel enrollment) {
        try (Connection conn = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword)) {
            // Use DAO to save enrollment into the database
            EnrollmentDAO dao = new EnrollmentDAO();
            return dao.saveEnrollment(conn, enrollment);
        } catch (Exception e) {
            System.out.println("EnrollmentService - Error in enrollStudent: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Retrieves a list of programs the user is enrolled in.
     *
     * @param userId The ID of the user
     * @return List of ProgramModel representing the enrolled programs
     */
    public List<ProgramModel> getUserEnrollments(Integer userId) {
        List<ProgramModel> enrollments = new ArrayList<>();

        // SQL query to fetch program details for a given user based on enrollments
        String sql = "SELECT p.program_Id, p.program_Name, p.program_Desc, p.image_path " +
                     "FROM program p " +
                     "JOIN enrollments e ON p.program_Id = e.Program_Id " +
                     "WHERE e.User_Id = ?";

        // Try-with-resources block to manage DB connection and statement
        try (Connection conn = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Set the user ID parameter in the query
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            // Process the result set and create a list of ProgramModel objects
            while (rs.next()) {
                ProgramModel program = new ProgramModel();
                program.setProgram_Id(rs.getInt("program_Id"));
                program.setProgram_Name(rs.getString("program_Name"));
                program.setProgram_Desc(rs.getString("program_Desc"));
                program.setImage_path(rs.getString("image_path"));
                enrollments.add(program);
            }
        } catch (Exception e) {
            System.out.println("EnrollmentService - Error in getUserEnrollments: " + e.getMessage());
            e.printStackTrace();
        }

        return enrollments;
    }
}
