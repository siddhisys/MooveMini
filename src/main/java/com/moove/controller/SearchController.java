package com.moove.controller;

import com.moove.model.ProgramModel;
import com.moove.service.SearchService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.List;

@WebServlet("/SearchController")
public class SearchController extends HttpServlet {
    private SearchService searchService;

    @Override
    public void init() throws ServletException {
        searchService = new SearchService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            // Sidebar: Best for Beginners
            List<ProgramModel> sidebar = searchService.getBeginnerPrograms();
            req.setAttribute("sidebarPrograms", sidebar);

            // Main content: Search or View All
            String search = req.getParameter("search");
            String viewAll = req.getParameter("viewAll");

            List<ProgramModel> displayed;
            if ("true".equals(viewAll)) {
                displayed = searchService.getAllPrograms();
            } else if (search != null && !search.trim().isEmpty()) {
                displayed = searchService.searchProgramsByName(search);
            } else {
                displayed = searchService.getAllPrograms(); // Default to all programs
            }

            req.setAttribute("displayedPrograms", displayed);
            req.setAttribute("searchQuery", search);

            req.getRequestDispatcher("/WEB-INF/pages/Search.jsp")
               .forward(req, resp);

        } catch (Exception e) {
            throw new ServletException("Error processing request", e);
        }
    }
}