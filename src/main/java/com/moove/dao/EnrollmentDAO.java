package com.moove.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.moove.model.EnrollmentModel;

/**
 * Data Access Object (DAO) class for handling enrollment-related database operations.
 */
public class EnrollmentDAO {

    /**
     * Saves a new enrollment record into the 'enrollments' table.
     *
     * @param conn       The database connection to use.
     * @param enrollment The EnrollmentModel object containing user and program IDs.
     * @return true if the insertion was successful, false otherwise.
     */
    public boolean saveEnrollment(Connection conn, EnrollmentModel enrollment) {
        // SQL query to insert a new enrollment record
        String sql = "INSERT INTO enrollments (User_Id, Program_Id) VALUES (?, ?)";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            // Set the values for User_Id and Program_Id in the query
            stmt.setInt(1, enrollment.getUser_Id());
            stmt.setInt(2, enrollment.getProgram_Id());

            // Execute the query and check if any rows were affected
            int rows = stmt.executeUpdate();
            return rows > 0;  // Return true if at least one row was inserted
        } catch (Exception e) {
            // Print error details if an exception occurs
            System.out.println("EnrollmentDAO - Error saving enrollment: " + e.getMessage());
            e.printStackTrace();
            return false;  // Return false if insertion failed
        }
    }
}
