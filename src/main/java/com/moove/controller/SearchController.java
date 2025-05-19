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
    private static final long serialVersionUID = 1L;
    private SearchService searchService;

    @Override
    public void init() throws ServletException {
        // Initialize the SearchService instance when servlet is first loaded
        searchService = new SearchService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        try {
            // Log request arrival time for debugging
            System.out.println("SearchController - Request received at: " + new java.util.Date());
            
            

            // Get query parameters: 'search' for search term, 'viewAll' to show all programs
            String search = req.getParameter("search");
            String viewAll = req.getParameter("viewAll");
            System.out.println("SearchController - Search Query: " + search);
            System.out.println("SearchController - View All: " + viewAll);

            List<ProgramModel> displayed;

            // Determine which list of programs to fetch based on parameters
            if ("true".equalsIgnoreCase(viewAll)) {
                // If viewAll is true, get all programs
                displayed = searchService.getAllPrograms();
                System.out.println("SearchController - Fetching all programs");
            } else if (search != null && !search.trim().isEmpty()) {
                // If search query is provided, perform search by program name
                displayed = searchService.searchProgramsByName(search);
                System.out.println("SearchController - Searching for: " + search);
            } else {
                // Default behavior: get all programs
                displayed = searchService.getAllPrograms();
                System.out.println("SearchController - Default: Fetching all programs");
            }

            // Log the number of programs to display
            System.out.println("SearchController - Displayed Programs: " + (displayed != null ? displayed.size() : "null"));
            
            // Add displayed program list and search query as request attributes for JSP rendering
            req.setAttribute("displayedPrograms", displayed);
            req.setAttribute("searchQuery", search);

            // Forward request and response to the JSP page for rendering results
            req.getRequestDispatcher("/WEB-INF/pages/Search.jsp")
               .forward(req, resp);

        } catch (Exception e) {
            // Log exception and rethrow as ServletException to trigger error handling
            System.out.println("SearchController - Exception: " + e.getMessage());
            throw new ServletException("Error processing request", e);
        }
    }
}
