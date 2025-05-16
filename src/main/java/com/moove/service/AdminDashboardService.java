package com.moove.service;

import com.moove.dao.ProgramDAO;
import com.moove.dao.UserDAO;
import com.moove.model.ProgramModel;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminDashboardService {
    private ProgramDAO programDAO;
    private UserDAO userDAO;
    private Connection conn;

    public AdminDashboardService(Connection conn) {
        this.conn = conn;
        this.programDAO = new ProgramDAO(conn);
        this.userDAO = new UserDAO(conn);
    }

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        try {
            // Total Programs and Program Levels
            List<ProgramModel> programs = programDAO.getAllPrograms();
            stats.put("totalPrograms", programs.size());
            Map<String, Integer> programLevels = new HashMap<>();
            programLevels.put("beginner", (int) programs.stream()
                    .filter(p -> p != null && p.getProgram_Level() != null && 
                                 p.getProgram_Level().toLowerCase().contains("beginner"))
                    .count());
            programLevels.put("intermediate", (int) programs.stream()
                    .filter(p -> p != null && p.getProgram_Level() != null && 
                                 p.getProgram_Level().toLowerCase().contains("intermediate"))
                    .count());
            programLevels.put("advanced", (int) programs.stream()
                    .filter(p -> p != null && p.getProgram_Level() != null && 
                                 p.getProgram_Level().toLowerCase().contains("advanced"))
                    .count());
            stats.put("programLevels", programLevels);
            System.out.println("Program Levels: " + programLevels);

            // Total Students and Instructors
            stats.put("totalStudents", userDAO.countUsersByRole("Student"));
            stats.put("totalInstructors", userDAO.countUsersByRole("Instructor"));
            System.out.println("Total Students: " + stats.get("totalStudents") + ", Total Instructors: " + stats.get("totalInstructors"));

            // User Distribution
            Map<String, Integer> activeUsers = userDAO.countUserStatus();
            stats.put("activeUsers", activeUsers != null ? activeUsers : new HashMap<>());
            System.out.println("Active Users: " + stats.get("activeUsers"));

        } catch (SQLException e) {
            System.err.println("Error in getDashboardStats: " + e.getMessage());
            e.printStackTrace();
            stats.put("totalPrograms", 0);
            stats.put("totalStudents", 0);
            stats.put("totalInstructors", 0);
            stats.put("programLevels", new HashMap<>());
            stats.put("activeUsers", new HashMap<>());
        }
        return stats;
    }

    public List<Map<String, String>> getEvents() {
        return List.of(
            Map.of("name", "Summer Gala 2025", "details", "Date - May 15, 2025, Location - Moove Hall"),
            Map.of("name", "New Year 2082", "details", "Date - Jan 1, 2082, Location - Moove Hall")
        );
    }

    public void closeConnection() {
        if (conn != null) {
            try {
                conn.close();
                System.out.println("Database connection closed at " + new java.util.Date());
            } catch (SQLException e) {
                System.err.println("Error closing connection: " + e.getMessage());
            }
        }
    }
}