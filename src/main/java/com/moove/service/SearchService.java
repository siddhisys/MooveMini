package com.moove.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

import com.moove.dao.ProgramDAO;
import com.moove.model.ProgramModel;

/**
 * Service class to handle searching and retrieving program data 
 * from the database for the Moove application.
 */
public class SearchService {

    // JDBC database connection details
    private final String jdbcURL = "jdbc:mysql://localhost:3306/moove";
    private final String jdbcUsername = "root";
    private final String jdbcPassword = "";

    /**
     * Retrieves all available programs from the database.
     * 
     * @return a list of all ProgramModel objects, or null if an error occurs
     */
    public List<ProgramModel> getAllPrograms() {
        try (Connection conn = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword)) {
            ProgramDAO dao = new ProgramDAO(conn); // DAO handles database queries
            return dao.getAllPrograms();
        } catch (Exception e) {
            System.out.println("SearchService - Error in getAllPrograms: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Retrieves all beginner-level programs (static list or filtered logic).
     * 
     * @return a list of beginner ProgramModel objects, or null if an error occurs
     */
    public List<ProgramModel> getBeginnerPrograms() {
        try (Connection conn = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword)) {
            ProgramDAO dao = new ProgramDAO(conn); // Initialize DAO
            return dao.getStaticBeginnerPrograms(); // Could be hardcoded or filtered logic
        } catch (Exception e) {
            System.out.println("SearchService - Error in getBeginnerPrograms: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Searches programs by their name using a keyword.
     * 
     * @param keyword the search keyword to filter program names
     * @return a list of matched ProgramModel objects, or null if an error occurs
     */
    public List<ProgramModel> searchProgramsByName(String keyword) {
        try (Connection conn = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword)) {
            ProgramDAO dao = new ProgramDAO(conn); // Instantiate DAO
            return dao.searchProgramsByName(keyword); // Perform search operation
        } catch (Exception e) {
            System.out.println("SearchService - Error in searchProgramsByName: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }
}
