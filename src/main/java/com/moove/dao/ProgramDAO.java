package com.moove.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.moove.model.ProgramModel;

/**
 * Data Access Object (DAO) for handling operations related to programs in the database.
 */
public class ProgramDAO {
    private Connection conn;

    /**
     * Constructor to initialize the ProgramDAO with a database connection.
     * 
     * @param conn The active database connection.
     */
    public ProgramDAO(Connection conn) {
        this.conn = conn;
    }

    /**
     * Retrieves all programs from the database.
     * 
     * @return A list of all ProgramModel objects.
     * @throws SQLException if any SQL error occurs.
     */
    public List<ProgramModel> getAllPrograms() throws SQLException {
        List<ProgramModel> list = new ArrayList<>();
        String sql = "SELECT * FROM program";
        
        // Prepare and execute the SQL statement
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            // Loop through the result set and populate the list
            while (rs.next()) {
                ProgramModel p = new ProgramModel();
                p.setProgram_Id(rs.getInt("Program_Id"));
                p.setProgram_Name(rs.getString("Program_Name"));
                p.setProgram_Level(rs.getString("Program_Level"));
                p.setProgram_Desc(rs.getString("Program_Desc"));
                p.setImage_path(rs.getString("image_path"));
                list.add(p);
            }
        }
        return list;
    }

    /**
     * Searches for programs by name using a case-insensitive, flexible keyword match.
     * Spaces and hyphens are ignored for better matching.
     * 
     * @param keyword The search keyword entered by the user.
     * @return A list of matching ProgramModel objects.
     * @throws SQLException if any SQL error occurs.
     */
    public List<ProgramModel> searchProgramsByName(String keyword) throws SQLException {
        List<ProgramModel> list = new ArrayList<>();
        
        // Normalize the search keyword: lowercase, remove spaces and hyphens
        String norm = keyword.toLowerCase().replaceAll("[\\s\\-]", "");
        
        // SQL query using normalized name matching
        String sql = ""
          + "SELECT * FROM program "
          + "WHERE LOWER(REPLACE(REPLACE(Program_Name,' ',''),'-','')) LIKE ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + norm + "%");
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    ProgramModel p = new ProgramModel();
                    p.setProgram_Id(rs.getInt("Program_Id"));
                    p.setProgram_Name(rs.getString("Program_Name"));
                    p.setProgram_Level(rs.getString("Program_Level"));
                    p.setProgram_Desc(rs.getString("Program_Desc"));
                    p.setImage_path(rs.getString("image_path"));
                    list.add(p);
                }
            }
        }
        return list;
    }

    /**
     * Retrieves two beginner-level programs, used for a static display (e.g., sidebar).
     * 
     * @return A list of two beginner ProgramModel objects.
     * @throws SQLException if any SQL error occurs.
     */
    public List<ProgramModel> getStaticBeginnerPrograms() throws SQLException {
        List<ProgramModel> list = new ArrayList<>();
        
        // SQL query to get 2 beginner-level programs
        String sql = "SELECT * FROM program WHERE Program_Level = 'Beginner' LIMIT 2";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                ProgramModel p = new ProgramModel();
                p.setProgram_Id(rs.getInt("Program_Id"));
                p.setProgram_Name(rs.getString("Program_Name"));
                p.setProgram_Level(rs.getString("Program_Level"));
                p.setProgram_Desc(rs.getString("Program_Desc"));
                p.setImage_path(rs.getString("image_path"));
                list.add(p);
            }
        }
        return list;
    }
}
