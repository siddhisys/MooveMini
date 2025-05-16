package com.moove.controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.moove.config.DbConfig;
import com.moove.model.ProgramModel;

@WebServlet("/ListPrograms")
public class ListProgramsController extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        List<ProgramModel> programs = new ArrayList<>();

        try {
            Connection conn = DbConfig.getDbConnection();
            String sql = "SELECT * FROM program ORDER BY Program_Name";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                ProgramModel program = new ProgramModel();
                program.setProgram_Id(rs.getInt("Program_Id"));
                program.setProgram_Name(rs.getString("Program_Name"));
                program.setProgram_Level(rs.getString("Program_Level"));
                program.setProgram_Classes(rs.getInt("Program_Classes"));
                program.setProgram_Desc(rs.getString("Program_Desc"));
                program.setImage_path(rs.getString("image_path"));
                programs.add(program);
            }

            rs.close();
            pstmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        request.setAttribute("programs", programs);
        request.getRequestDispatcher("/WEB-INF/pages/AdminProgram.jsp").forward(request, response);    }
}