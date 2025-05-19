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
<style>
    .message { display: none; } /* Hide the old message */
    .error { color: #e74c3c; text-align: center; margin-top: 10px; }
</style>
<script>
    function showEnrollmentPopup() {
        alert("${successEnrollmentMessage}");
    }
    window.onload = function() {
        <c:if test="${not empty successEnrollmentMessage}">
            showEnrollmentPopup();
        </c:if>
    };
</script>
</head>
<body>

<jsp:include page="Header.jsp"/>

<div class="container">
    <div class="sidebar">
        <h2>What our students say</h2>
        <div class="testimonial-container">
            <div class="testimonial-card">
                <img src="${pageContext.request.contextPath}/resources/images/Search/avatar1.jpeg" alt="Aayush" class="testimonial-avatar">
                <blockquote>"The hip-hop teens class was the best part of my summer!"</blockquote>
                <p>— Aayush, 14</p>
            </div>
            <div class="testimonial-card">
                <img src="${pageContext.request.contextPath}/resources/images/Search/avatar2.jpeg" alt="Priya" class="testimonial-avatar">
                <blockquote>"Amazing instructors and a fun learning environment!"</blockquote>
                <p>— Priya, 16</p>
            </div>
            <div class="testimonial-card">
                <img src="${pageContext.request.contextPath}/resources/images/Search/avatar3.jpeg" alt="Rohan" class="testimonial-avatar">
                <blockquote>"I gained so much confidence through the ballet classes!"</blockquote>
                <p>— Rohan, 18</p>
            </div>
            <div class="testimonial-card">
                <img src="${pageContext.request.contextPath}/resources/images/Search/avatar4.jpeg" alt="Sneha" class="testimonial-avatar">
                <blockquote>"The best dance experience I've ever had!"</blockquote>
                <p>— Sneha, 22</p>
            </div>
        </div>
    </div>

    <div class="main-content">
        <div class="search-bar">
            <form action="${pageContext.request.contextPath}/SearchController" method="get" id="searchForm">
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
                    <form action="${pageContext.request.contextPath}/EnrollmentController" method="post">
                        <input type="hidden" name="programId" value="${program.program_Id}" />
                        <button type="submit" class="enroll-button">Enroll</button>
                    </form>
                </div>
            </c:forEach>
        </div>
        <c:if test="${not empty error}">
            <p class="error">${error}</p>
        </c:if>
    </div>
</div>

<jsp:include page="Footer.jsp"/>

<script>
    document.getElementById("searchForm").addEventListener("submit", function(event) {
        console.log("Form submitted!");
        console.log("Search query:", document.querySelector("input[name='search']").value);
        console.log("View All:", document.querySelector("button[name='viewAll']") ? "true" : "false");
    });
</script>
</body>
</html>