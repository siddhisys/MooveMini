<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%
    String role = null;
    if (session.getAttribute("role") != null) {
        role = (String) session.getAttribute("role");
    }
%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Header</title>
    <link rel="stylesheet" href="CSS/Header.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.0/css/all.min.css">
    <link href="https://fonts.googleapis.com/css2?family=Playfair+Display&display=swap" rel="stylesheet">
</head>
<body>
    <header class="main-header<%= "Admin".equals(role) ? " admin-header" : "" %>">
        <div class="header-left">
            <% if ("Admin".equals(role)) { %>
                <a href="${pageContext.request.contextPath}/AdminDashboard">Dashboard</a>
                <a href="${pageContext.request.contextPath}/UserList">User List</a>
                <a href="${pageContext.request.contextPath}/ListPrograms">Program List</a>
            <% } else { %>
                <a href="${pageContext.request.contextPath}/aboutus">About Us</a>
                <a href="${pageContext.request.contextPath}/contact">Contact Us</a>
            <% } %>
        </div>

        <div class="header-center">
            <div class="logo">MOOVE</div>
        </div>

        <div class="header-right">
            <% if (!"Admin".equals(role)) { %>
                <form action="${pageContext.request.contextPath}/SearchController" method="get" class="search-form">
                    <input type="text" name="search" placeholder="Search programs..." />
                    <button type="submit"><i class="fas fa-search"></i></button>
                </form>
            <% } %>
            <div class="profile-icon">
                <a href="${pageContext.request.contextPath}/profile">
                    <i class="fas fa-user"></i>
                </a>
            </div>
            <div class="logout-icon">
                <a href="${pageContext.request.contextPath}/Logout"
                   title="Logout"
                   onclick="return confirm('Are you sure you want to log out?');">
                    <i class="fas fa-sign-out-alt"></i>
                </a>
            </div>
        </div>
    </header>
</body>
</html>
