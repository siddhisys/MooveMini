package com.moove.controller;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.UUID;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import com.moove.config.DbConfig;

@WebServlet("/AddProgram")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,
    maxFileSize = 1024 * 1024 * 10,
    maxRequestSize = 1024 * 1024 * 30
)
public class AddProgramController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        boolean success = false;
        String message = "";

        try {
            String name = request.getParameter("programName");
            String level = request.getParameter("programLevel");
            int classes = Integer.parseInt(request.getParameter("programClasses"));
            String desc = request.getParameter("programDesc");
            Part filePart = request.getPart("imagePath");
            String fileName = UUID.randomUUID().toString() + "." + getFileExtension(filePart);

            String uploadPath = getServletContext().getRealPath("") + File.separator + "resources" + File.separator + "images" + File.separator + "programs";
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) uploadDir.mkdirs();
            filePart.write(uploadPath + File.separator + fileName);

            Connection conn = DbConfig.getDbConnection();
            String sql = "INSERT INTO program (Program_Name, Program_Level, Program_Classes, Program_Desc, image_path) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, name);
            pstmt.setString(2, level);
            pstmt.setInt(3, classes);
            pstmt.setString(4, desc);
            pstmt.setString(5, fileName);
            int rows = pstmt.executeUpdate();

            success = rows > 0;
            message = success ? "Program added successfully" : "Failed to add program";

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

    private String getFileExtension(Part part) {
        String contentDisp = part.getHeader("content-disposition");
        String fileName = contentDisp.substring(contentDisp.lastIndexOf("=") + 2, contentDisp.length() - 1);
        return fileName.substring(fileName.lastIndexOf(".") + 1);
    }

    private String escapeJson(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}