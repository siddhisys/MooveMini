<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Moove Login</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CSS/Login.css">
</head>
<body>
<div class="left">
<img src="${pageContext.request.contextPath}/resources/images/LoginPage/LoginGIF.gif" class="gif"/>
</div>

<div class="right">
<div class="form-container">
<h1>LOG IN</h1>

<div class="social-buttons">
<button class="social-btn google-btn">
<img src="${pageContext.request.contextPath}/resources/images/RegisterPage/googleicon.png" alt="Google" class="social-icon">
Sign in with Google
</button>

<button class="social-btn facebook-btn">
<img src="${pageContext.request.contextPath}/resources/images/RegisterPage/facebookicon.png" alt="Facebook" class="social-icon">
Sign in with FaceBook
</button>
</div>

<div class="or-divider">--OR--</div>

<div class="form-section">
<form action="${pageContext.request.contextPath}/Login" method="POST">
<div class="form-group">
<label for="username">UserName</label>
<input type="text" id="username" name="username" placeholder="UserName" required>
</div>

<div class="form-group">
<label for="password">Password</label>
<input type="password" id="password" name="password" placeholder="Password" required>
</div>

<button type="submit" class="login-btn">LOG IN</button>

<div class="forgotpw-link">
<a href="${pageContext.request.contextPath}/login">Forgot Password?</a>
</div>

<% if (request.getAttribute("success") != null) { %>
<div style="color: green; text-align: center; margin-top: 15px;">
<%= request.getAttribute("success") %>
</div>
<% } else if (request.getAttribute("error") != null) { %>
<div style="color: red; text-align: center; margin-top: 15px;">
<%= request.getAttribute("error") %>
</div>
<% } %>

</form>
</div>
</div>

<a href="${pageContext.request.contextPath}/Register" class="back-btn">&lt;</a>
</div>
</body>
</html>