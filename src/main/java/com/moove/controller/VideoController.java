package com.moove.controller; 

import java.io.*;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import jakarta.servlet.annotation.WebServlet;

@WebServlet("/video/*")
public class VideoController extends HttpServlet {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        // Get the video filename from the URL
        String requestedFile = request.getPathInfo();
        
        if (requestedFile == null || requestedFile.equals("/")) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        
        // Remove leading slash if present
        if (requestedFile.startsWith("/")) {
            requestedFile = requestedFile.substring(1);
        }
        
        // For security reasons, only allow specific files from specific folders
        if (!requestedFile.matches("^[a-zA-Z0-9_\\-\\.]+\\.(mp4|webm)$")) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }
        
        // Get the real path to the resources folder
        String applicationPath = request.getServletContext().getRealPath("");
        String videoFolderPath = applicationPath + File.separator + "resources" + 
                                 File.separator + "images" + File.separator + "Search";
        
        // Create File object for the requested video
        File videoFile = new File(videoFolderPath, requestedFile);
        
        // Log the full path for debugging
        System.out.println("Attempting to serve video from: " + videoFile.getAbsolutePath());
        
        // Check if file exists and is accessible
        if (!videoFile.exists() || !videoFile.isFile() || !videoFile.canRead()) {
            System.out.println("Video file not found or not accessible: " + videoFile.getAbsolutePath());
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }
        
        // Determine content type
        String contentType;
        if (requestedFile.endsWith(".mp4")) {
            contentType = "video/mp4";
        } else if (requestedFile.endsWith(".webm")) {
            contentType = "video/webm"; 
        } else {
            contentType = "application/octet-stream";
        }
        
        // Set content type and headers
        response.setContentType(contentType);
        response.setContentLengthLong(videoFile.length());
        response.setHeader("Content-Disposition", "inline; filename=\"" + requestedFile + "\"");
        
        // Stream the file
        try (InputStream in = new FileInputStream(videoFile);
             OutputStream out = response.getOutputStream()) {
            
            byte[] buffer = new byte[8192]; // 8KB buffer
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
            System.err.println("Error serving video: " + e.getMessage());
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}