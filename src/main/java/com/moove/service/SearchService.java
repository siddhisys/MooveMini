package com.moove.service;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

import com.moove.dao.ProgramDAO;
import com.moove.model.ProgramModel;

public class SearchService {

    private final String jdbcURL = "jdbc:mysql://localhost:3306/moove";
    private final String jdbcUsername = "root";
    private final String jdbcPassword = "";

    public List<ProgramModel> getAllPrograms() {
        try (Connection conn = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword)) {
            ProgramDAO dao = new ProgramDAO(conn);
            return dao.getAllPrograms();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<ProgramModel> getBeginnerPrograms() {
        try (Connection conn = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword)) {
            ProgramDAO dao = new ProgramDAO(conn);
            return dao.getStaticBeginnerPrograms();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<ProgramModel> searchProgramsByName(String keyword) {
        try (Connection conn = DriverManager.getConnection(jdbcURL, jdbcUsername, jdbcPassword)) {
            ProgramDAO dao = new ProgramDAO(conn);
            return dao.searchProgramsByName(keyword);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}