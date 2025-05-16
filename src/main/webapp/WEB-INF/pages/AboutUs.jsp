<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>

<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CSS/Header.css">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CSS/Footer.css">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CSS/AboutUs.css">

    <meta charset="UTF-8">
    <title>About Us - Moove</title>
 
</head>
<body>

<jsp:include page="Header.jsp"/>
    
    <!-- Main Content -->
    <main>
        <!-- Hero Section -->
        <section class="hero-section">
            <div class="hero-content">
                <h1>Show your moves on Moove</h1>
                <div class="dancer-illustration">
                    <img src="${pageContext.request.contextPath}/resources/images/AboutUs/ballet.gif" alt="GIF">
                </div>
            </div>
        </section>
        
        <!-- Foundational Ingredients Section -->
        <section class="ingredients-section">
            <div class="content-wrapper">
                <div class="founders-image">
                    <img src="${pageContext.request.contextPath}/resources/images/AboutUs/name.png" alt="Mieth and Suish">
                </div>
                <div class="founders-story">
                    <h2>Our Foundational Ingredients</h2>
                    <p>Moove was born from the shared vision of Mieth and Suish—a vision fueled by creativity, culture, and community. For over a decade, they’ve pursued a bold idea: to create a movement where dance isn't just performance, but passion, wellness, and self-expression. MOOVE brings together artistry and energy, drawing strength from its foundations—hard work, authenticity, and a deep respect for diverse dance traditions.</p>
                </div>
            </div>
        </section>
        
        <!-- Mission Section -->
        <section class="mission-section">
            <h2>Our Mission</h2>
            <div class="mission-values">
                <div class="mission-card">
                    <div class="mission-icon">
                        <img src="${pageContext.request.contextPath}/resources/images/AboutUs/community.png" alt="People together icon">
                    </div>
                    <h3>Bringing people together</h3>
                </div>
                
                <div class="mission-card">
                    <div class="mission-icon">
                        <img src="${pageContext.request.contextPath}/resources/images/AboutUs/envfriendly.png" alt="Environmental icon">
                    </div>
                    <h3>Environmental friendly</h3>
                </div>
                
                <div class="mission-card">
                    <div class="mission-icon">
                        <!-- Add your mission icon path here -->
                        <img src="${pageContext.request.contextPath}/resources/images/AboutUs/coupledancing.png" alt="Cultures icon">
                    </div>
                    <h3>Re-inventing Cultures</h3>
                </div>
            </div>
        </section>
    </main>
    
<jsp:include page="Footer.jsp"/>    
</body>
</html>