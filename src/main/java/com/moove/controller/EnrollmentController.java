package com.moove.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

import com.moove.model.EnrollmentModel;
import com.moove.model.UsersModel;
import com.moove.model.ProgramModel;
import com.moove.service.EnrollmentService;

/**
 * Servlet implementation class EnrollmentController
 * 
 * Handles enrollment requests for users wishing to enroll in a program.
 */
@WebServlet("/EnrollmentController")
public class EnrollmentController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	// Service layer object to handle enrollment logic
	private EnrollmentService enrollmentService;
	
	@Override
    public void init() throws ServletException {
        // Initialize EnrollmentService once when the servlet is loaded
        if (enrollmentService == null) {
            enrollmentService = new EnrollmentService();
            System.out.println("EnrollmentController - init() called, enrollmentService initialized.");
        }
    }
	
	/**
	 * Handles HTTP POST requests to enroll a logged-in user in a program.
	 * 
	 * @param request  HttpServletRequest object containing the client request
	 * @param response HttpServletResponse object for sending response back
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Obtain the current HTTP session
		HttpSession session = request.getSession();

        // Retrieve the logged-in user from session
        UsersModel loggedInUser = (UsersModel) session.getAttribute("loggedInUser");
        
        // If no user is logged in, redirect to login page with an error message
        if (loggedInUser == null) {
            request.setAttribute("error", "Please log in to enroll.");
            request.getRequestDispatcher("/WEB-INF/pages/Login.jsp").forward(request, response);
            return;  // Stop further processing
        }

        // Retrieve the program ID from the POST request parameter
        Integer programId = Integer.parseInt(request.getParameter("programId"));
        
        // Get the user ID from the logged-in user object
        Integer userId = loggedInUser.getUser_ID(); // Assuming UsersModel has getUser_ID()

        // Create a new EnrollmentModel instance representing the enrollment request
        EnrollmentModel enrollment = new EnrollmentModel(userId, programId);
        
        // Call the service layer to attempt enrolling the user in the program
        boolean success = enrollmentService.enrollStudent(enrollment);

        // Based on success/failure, set appropriate feedback messages and update session
        if (success) {
        	// Fetch the updated list of programs the user is enrolled in
            List<ProgramModel> userEnrollments = enrollmentService.getUserEnrollments(userId); // Make sure this method exists
            
            // Store the updated enrollment list in session for use in JSPs or other views
            session.setAttribute("userEnrollments", userEnrollments);
            
            // Set a success message to display on the next page
            request.setAttribute("successEnrollmentMessage", "Let's start mooving now! Check your profile for details!");
        } else {
            // Enrollment failed, set an error message to display
            request.setAttribute("error", "Enrollment failed. Please try again.");
        }

        // Forward the request back to the Search.jsp page for user to see the result
        request.getRequestDispatcher("/WEB-INF/pages/Search.jsp").forward(request, response);
    }
}
