<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>

<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CSS/Header.css">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CSS/Footer.css">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CSS/UserDashboard.css">

<meta charset="UTF-8">
<title>Dashboard | MOOVE</title>
</head>
<body>

<jsp:include page="Header.jsp"/>

<div class="dashboard">

    <div class="banner">
        <img src="${pageContext.request.contextPath}/resources/images/UserDashboard/banner.jpeg" alt="Banner">
    </div>

    <div class="section">
        <h2>Upcoming Events</h2>
        <div class="card-container">
            <div class="card">
                <img src="${pageContext.request.contextPath}/resources/images/UserDashboard/Bharatnatyam.jpeg" alt="Event 1">
                <h3>Bharatanatyam Showcase</h3>
                <p>Join us for an exciting evening of Bharatanatyam dance.</p>
            </div>
            <div class="card">
                <img src="${pageContext.request.contextPath}/resources/images/UserDashboard/Ballet.jpeg" alt="Event 2">
                <h3>Ballet Night</h3>
                <p>Experience the grace and elegance of our ballet dancers.</p>
            </div>
            <div class="card">
                <img src="${pageContext.request.contextPath}/resources/images/UserDashboard/Hiphop.jpeg" alt="Event 3">
                <h3>Hip-Hop Battle</h3>
                <p>Witness the thrilling moves in our hip-hop battle event.</p>
            </div>
        </div>
    </div>

    <div class="section">
        <h2>All Time Favourites</h2>
        <div class="card-container">
            <div class="card">
                <img src="${pageContext.request.contextPath}/resources/images/UserDashboard/Salsa.png" alt="Favourite 1">
                <h3>Salsa Basics</h3>
                <p>Master the rhythms and moves of salsa with our expert instructors.</p>
            </div>
            <div class="card">
                <img src="${pageContext.request.contextPath}/resources/images/UserDashboard/Ballet.jpeg" alt="Favourite 2">
                <h3>Ballet Intensive</h3>
                <p>Refine your technique and grace with our intensive ballet program.</p>
            </div>
            <div class="card">
                <img src="${pageContext.request.contextPath}/resources/images/UserDashboard/Hiphop.jpeg" alt="Favourite 3">
                <h3>Hip-Hop Basics</h3>
                <p>Learn the foundations of hip-hop with our dynamic classes.</p>
            </div>
        </div>
    </div>

</div>

<jsp:include page="Footer.jsp"/>

</body>
</html>
