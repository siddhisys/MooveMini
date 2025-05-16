<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Moove Landing Page</title>

<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CSS/LandingPage.css">
<link href="https://fonts.cdnfonts.com/css/the-seasons" rel="stylesheet">
</head>

<body>
<h4>GROOVE WITH MOOVE</h4>
<h1>Moove</h1>
<h5>I like art, and by art  I mean music, poetry,<br>
	romance, paintings, the human body,<br>
	literature. All of this is art to me.</h5>
	
<a href="${pageContext.request.contextPath}/Register">
    <button>GET STARTED</button>
</a>

<div class="social-icons">
	<img src="${pageContext.request.contextPath}/resources/images/LandingPage/facebook.png" alt="Facebook">
    <img src="${pageContext.request.contextPath}/resources/images/LandingPage/instagram.png" alt="Instagram">
    <img src="${pageContext.request.contextPath}/resources/images/LandingPage/twitter.png" alt="Twitter">	
</div>

</body>
</html>