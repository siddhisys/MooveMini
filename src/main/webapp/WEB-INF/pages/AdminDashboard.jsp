<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html>
<head>
    <title>Moove Dashboard</title>
    <meta http-equiv="refresh" content="30">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CSS/Header.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CSS/Footer.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CSS/AdminDashboard.css">
    <link href="https://fonts.googleapis.com/css2?family=Poppins:wght@300;400;500;600;700&display=swap" rel="stylesheet">
</head>
<body>
<div class="dashboard-wrapper">
    <jsp:include page="Header.jsp"/>

    <div class="main-content">
        <div class="content-area">
            <div class="welcome-section">
                <h1>Hello admin! Welcome back to Moove.</h1>
            </div>

            <div class="dashboard-analytics">
                <h2>Analytics</h2>
                <div class="stats-grid">
                    <div class="stat-card">
                        <div class="stat-content">
                            <div class="stat-title">Users</div>
                            <div class="stat-value">${stats.totalStudents != null ? stats.totalStudents : 10}</div> <!-- Default value -->
                        </div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-content">
                            <div class="stat-title">Total Programs</div>
                            <div class="stat-value">${stats.totalPrograms != null ? stats.totalPrograms : 5}</div> <!-- Default value -->
                        </div>
                    </div>
                    <div class="stat-card">
                        <div class="stat-content">
                            <div class="stat-title">Total Instructors</div>
                            <div class="stat-value">${stats.totalInstructors != null ? stats.totalInstructors : 3}</div> <!-- Default value -->
                        </div>
                    </div>
                </div>
            </div>

            <div class="program-levels">
                <h2>Program Levels</h2>
                <div class="level-container">
                    <div class="level-card">${stats.programLevels.beginner != null ? stats.programLevels.beginner : 2} Beginner</div>
                    <div class="level-card">${stats.programLevels.advanced != null ? stats.programLevels.advanced : 1} Advanced</div>
                    <div class="level-card">${stats.programLevels.intermediate != null ? stats.programLevels.intermediate : 2} Intermediate</div>
                </div>
            </div>

            <div class="chart-section">
                <div class="chart-container">
                    <h2>Analytics</h2>
                    <canvas id="pieChart"></canvas>
                </div>
            </div>

            <div class="events-section">
                <h2>Event Management</h2>
                <div class="event-container">
                    <c:forEach var="event" items="${events}">
                        <div class="event-item">
                            <div class="event-info">
                                <div class="event-name">${event.name}</div>
                            </div>
                            <button class="view-btn" onclick="showPopup('${event.name}', '${event.details != null ? event.details : ''}')">View</button>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </div>
    </div>

    <jsp:include page="Footer.jsp"/>
</div>

<div id="popup" class="popup">
    <div class="popup-content">
        <span class="close-btn" onclick="hidePopup()">×</span>
        <h3 id="event-title"></h3>
        <p id="event-details"></p>
        <p class="notification">Note: You cannot edit this information as you were not the creator of this event.</p>
    </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
<script src="https://cdn.jsdelivr.net/npm/chartjs-plugin-datalabels@2"></script>
<script>
    const ctxPie = document.getElementById('pieChart').getContext('2d');
    new Chart(ctxPie, {
        type: 'doughnut',
        data: {
            labels: ['Active', 'Inactive', 'Suspended'],
            datasets: [{
                data: [
                    ${stats.activeUsers.active != null ? stats.activeUsers.active : 25}, // Default value
                    ${stats.activeUsers.inactive != null ? stats.activeUsers.inactive : 1}, // Default value
                    ${stats.activeUsers.suspended != null ? stats.activeUsers.suspended : 1} // Default value
                ],
                backgroundColor: ['#A8D5E2', '#F4C2C2', '#D4A5E5'],
                borderColor: '#F5F5F5',
                borderWidth: 2,
                hoverOffset: 15
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            cutout: '65%',
            plugins: {
                legend: { 
                    position: 'right', 
                    labels: { 
                        padding: 20, 
                        font: { size: 14, family: "'Poppins', sans-serif" }, 
                        usePointStyle: true, 
                        pointStyle: 'circle' 
                    } 
                },
                datalabels: { 
                    color: '#333', 
                    formatter: (value) => value > 0 ? value : '', 
                    font: { weight: 'bold', size: 14, family: "'Poppins', sans-serif" } 
                },
                tooltip: {
                    backgroundColor: 'rgba(245, 245, 245, 0.9)',
                    padding: 12,
                    cornerRadius: 6,
                    titleFont: { size: 14, family: "'Poppins', sans-serif", weight: 'bold' },
                    bodyFont: { size: 13, family: "'Poppins', sans-serif" },
                    callbacks: {
                        label: function(context) {
                            const total = context.dataset.data.reduce((a, b) => a + b, 0);
                            const value = context.raw;
                            const percentage = total > 0 ? Math.round((value * 100) / total) + '%' : '0%';
                            return `${context.label}: ${percentage} (${value.toLocaleString()})`;
                        }
                    }
                }
            },
            animation: { animateScale: true, animateRotate: true }
        },
        plugins: [ChartDataLabels]
    });

    function showPopup(eventName, eventDetails) {
        document.getElementById('event-title').textContent = eventName;
        document.getElementById('event-details').textContent = eventDetails || 'No details available';
        document.getElementById('popup').style.display = 'block';
    }

    function hidePopup() {
        document.getElementById('popup').style.display = 'none';
    }
</script>
</body>
</html>