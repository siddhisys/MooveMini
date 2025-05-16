package com.moove.util;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.net.URLDecoder;
import java.util.Arrays;

/**
 * Utility class for managing cookies in a web application.
 * Provides methods to add, retrieve, and delete cookies.
 */
public class CookieUtil {

    /**
     * Adds a cookie with the specified name, value, and maximum age.
     *
     * @param response the HttpServletResponse to add the cookie to
     * @param name     the name of the cookie
     * @param value    the value of the cookie
     * @param maxAge   the maximum age of the cookie in seconds
     */
    public static void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        try {
            // Encode the cookie value to handle spaces and special characters
            String encodedValue = URLEncoder.encode(value, "UTF-8").replace("+", "%20");
            Cookie cookie = new Cookie(name, encodedValue);
            cookie.setMaxAge(maxAge);
            cookie.setPath("/"); // Make cookie available to the entire application
            response.addCookie(cookie);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace(); // Log the error
            // Fallback: Set a default or empty cookie if encoding fails
            Cookie cookie = new Cookie(name, "");
            cookie.setMaxAge(maxAge);
            cookie.setPath("/");
            response.addCookie(cookie);
        }
    }

    /**
     * Retrieves a cookie by its name from the HttpServletRequest and decodes its value.
     *
     * @param request the HttpServletRequest to get the cookie from
     * @param name    the name of the cookie to retrieve
     * @return the decoded value of the Cookie if found, otherwise null
     */
    public static String getCookieValue(HttpServletRequest request, String name) {
        Cookie cookie = getCookie(request, name);
        if (cookie != null) {
            try {
                return URLDecoder.decode(cookie.getValue(), "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                return cookie.getValue(); // Return unencoded value as fallback
            }
        }
        return null;
    }

    /**
     * Retrieves a cookie by its name from the HttpServletRequest.
     *
     * @param request the HttpServletRequest to get the cookie from
     * @param name    the name of the cookie to retrieve
     * @return the Cookie object if found, otherwise null
     */
    public static Cookie getCookie(HttpServletRequest request, String name) {
        if (request.getCookies() != null) {
            return Arrays.stream(request.getCookies())
                    .filter(cookie -> name.equals(cookie.getName()))
                    .findFirst()
                    .orElse(null);
        }
        return null;
    }

    /**
     * Deletes a cookie by setting its max age to 0.
     *
     * @param response the HttpServletResponse to add the deletion cookie to
     * @param name     the name of the cookie to delete
     */
    public static void deleteCookie(HttpServletResponse response, String name) {
        Cookie cookie = new Cookie(name, null);
        cookie.setMaxAge(0);
        cookie.setPath("/"); // Make cookie available to the entire application
        response.addCookie(cookie);
    }
}