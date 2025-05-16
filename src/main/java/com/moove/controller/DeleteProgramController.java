package com.moove.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.moove.config.DbConfig;

@WebServlet("/DeleteProgram")
public class DeleteProgramController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        boolean success = false;
        String message = "";

        try {
            String name = request.getParameter("name");
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Program name is required");
            }

            Connection conn = DbConfig.getDbConnection();
            String sql = "DELETE FROM program WHERE Program_Name = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            int rows = pstmt.executeUpdate();

            success = rows > 0;
            message = success ? "Program deleted successfully" : "No program found with name: " + name;

            pstmt.close();
            conn.close();
        } catch (Exception e) {
            success = false;
            message = "Error: " + e.getMessage();
            e.printStackTrace();
        }

        String json = "{"
                + "\"success\":" + success + ","
                + "\"message\":\"" + escapeJson(message) + "\""
                + "}";
        out.print(json);
        out.flush();
    }

    private String escapeJson(String s) {
        return (s != null) ? s.replace("\\", "\\\\").replace("\"", "\\\"") : "";
    }
}