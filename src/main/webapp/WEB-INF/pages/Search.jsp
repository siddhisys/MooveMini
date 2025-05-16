<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Programs - Moove</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/Header.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/Footer.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/CSS/Search.css">
</head>
<body>

<jsp:include page="Header.jsp"/>

<div class="container">
<!-- Sidebar: Static "Watch Me" Section -->
<div class="sidebar">
<h2>Watch Me</h2>
<div class="video-card">
<video id="danceVideo" width="100%" height="auto" controls preload="auto" poster="${pageContext.request.contextPath}/resources/images/Search/video-poster.jpg">
    <!-- Use our new VideoServlet instead of direct file access -->
    <source src="${pageContext.request.contextPath}/video/dance.mp4" type="video/mp4"></video>
<p>Look at how hip-hop classes take place, for all levels.</p>
</div>
</div>

<!-- Main Content: Search + Results -->
<div class="main-content">
<div class="search-bar">
<form action="${pageContext.request.contextPath}/SearchController" method="get">
<input type="text" name="search" placeholder="Search programs..." value="${searchQuery != null ? searchQuery : ''}">
<button type="submit" class="search-button">Search</button>
<button type="submit" name="viewAll" value="true" class="view-all">View All</button>
</form>
</div>

<div class="program-grid">
<c:if test="${empty displayedPrograms}">
<p>No programs found.</p>
</c:if>
<c:forEach var="program" items="${displayedPrograms}">
<div class="program-card">
<img src="${pageContext.request.contextPath}/resources/images/programs/${program.image_path}" alt="${program.program_Name}" />
<h3>${program.program_Name}</h3>
<p>${program.program_Desc}</p>
</div>
</c:forEach>
</div>
</div>
</div>

<jsp:include page="Footer.jsp"/>
</body>
</html>