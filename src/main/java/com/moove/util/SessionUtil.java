package com.moove.util;

import jakarta.servlet.http.HttpServletRequest;

/**
 * Utility class for handling session-related operations.
 */
public class SessionUtil {
    
    /**
     * Sets an attribute in the session.
     * 
     * @param request The HTTP request
     * @param name The attribute name
     * @param value The attribute value
     */
    public static void setAttribute(HttpServletRequest request, String name, Object value) {
        request.getSession(true).setAttribute(name, value);
    }
    
    /**
     * Gets an attribute from the session.
     * 
     * @param request The HTTP request
     * @param name The attribute name
     * @return The attribute value, or null if not found
     */
    public static Object getAttribute(HttpServletRequest request, String name) {
        return request.getSession(false) != null ? request.getSession().getAttribute(name) : null;
    }
    
    /**
     * Removes an attribute from the session.
     * 
     * @param request The HTTP request
     * @param name The attribute name
     */
    public static void removeAttribute(HttpServletRequest request, String name) {
        if (request.getSession(false) != null) {
            request.getSession().removeAttribute(name);
        }
    }
    
    /**
     * Invalidates the session.
     * 
     * @param request The HTTP request
     */
    public static void invalidateSession(HttpServletRequest request) {
        if (request.getSession(false) != null) {
            request.getSession().invalidate();
        }
    }
}