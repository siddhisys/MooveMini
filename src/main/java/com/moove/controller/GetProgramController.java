package com.moove.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import com.moove.config.DbConfig;

@WebServlet("/GetProgram")
public class GetProgramController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        StringBuilder json = new StringBuilder();
        boolean success = false;
        String message = "";

        try {
            String name = request.getParameter("name");
            if (name == null || name.trim().isEmpty()) {
                throw new IllegalArgumentException("Program name is required");
            }

            Connection conn = DbConfig.getDbConnection();
            String sql = "SELECT * FROM program WHERE Program_Name = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                success = true;
                System.out.println("GetProgramController - Program_Name: " + rs.getString("Program_Name"));
                System.out.println("GetProgramController - Program_Classes: " + rs.getInt("Program_Classes"));
                json.append("{")
                    .append("\"program_Name\":\"").append(escapeJson(rs.getString("Program_Name"))).append("\",")
                    .append("\"program_Level\":\"").append(escapeJson(rs.getString("Program_Level"))).append("\",")
                    .append("\"program_Classes\":").append(rs.getInt("Program_Classes")).append(",")
                    .append("\"program_Desc\":\"").append(escapeJson(rs.getString("Program_Desc"))).append("\",")
                    .append("\"image_path\":\"").append(escapeJson(rs.getString("image_path"))).append("\",")
                    .append("\"success\":true")
                    .append("}");
            } else {
                message = "Program not found with name: " + name;
            }

            rs.close();
            pstmt.close();
            conn.close();
        } catch (Exception e) {
            message = "Error: " + e.getMessage();
            e.printStackTrace();
        }

        if (!success) {
            json.append("{")
                .append("\"success\":false,")
                .append("\"message\":\"").append(escapeJson(message)).append("\"")
                .append("}");
        }

        out.print(json.toString());
        out.flush();
    }

    private String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "\\r");
    }
}