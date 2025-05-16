package com.moove.dao;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import com.moove.model.ProgramModel;

public class ProgramDAO {
    private Connection conn;

    public ProgramDAO(Connection conn) {
        this.conn = conn;
    }

    public List<ProgramModel> getAllPrograms() throws SQLException {
        List<ProgramModel> list = new ArrayList<>();
        String sql = "SELECT * FROM program";
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

    /** Flexible, case-insensitive, ignores spaces/hyphens */
    public List<ProgramModel> searchProgramsByName(String keyword) throws SQLException {
        List<ProgramModel> list = new ArrayList<>();
        String norm = keyword.toLowerCase().replaceAll("[\\s\\-]", "");
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

    /** Two hard-coded “beginner” programs for sidebar */
    public List<ProgramModel> getStaticBeginnerPrograms() throws SQLException {
        List<ProgramModel> list = new ArrayList<>();
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
