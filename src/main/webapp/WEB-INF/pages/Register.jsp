<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page language="java" contentType="text/html; charset=UTF-8"
pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
	<title>Moove Register</title>
	<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CSS/Register.css">
</head>
<body>
	<div class="left">
		<img src="${pageContext.request.contextPath}/resources/images/RegisterPage/RegisterGIF.gif" class="gif"/>
	</div>

	<div class="right">
  <div class="form-container">
    <h1>CREATE ACCOUNT</h1>

 

    <div class="form-section">
  <form action="${pageContext.request.contextPath}/Register" method="POST" enctype="multipart/form-data">
  <div class="form-group">
    <input type="text" id="username" name="username" placeholder="UserName" required pattern="^[a-zA-Z][a-zA-Z ]{4,19}$" title="Name can only contain letters">
  </div>

  <div class="form-group">
    <input type="password" id="password" name="password" placeholder="Password" required>
  </div>

  <div class="form-group">
    <input type="email" id="email" name="email" placeholder="Email" required>
  </div>

  <div class="form-group">
    <input type="text" id="address" name="address" required pattern="^[a-zA-Z0-9 ,.-]+$" title="Name can only contain letters, digits, spaces, commas, dots, and hyphens" oninput="validateAddress()" placeholder="Address">
    <span id="address-error" class="error-message"></span>
  </div>

  <div class="form-group dropdown-row">
    <select id="gender" name="gender" required>
      <option value="" disabled selected>Gender</option>
      <option value="Male">Male</option>
      <option value="Female">Female</option>
      <option value="Other">Other</option>
    </select>

    <select id="role" name="role" required>
      <option value="" disabled selected>Role</option>
      <option value="Instructor">Instructor</option>
      <option value="Student">Student</option>
      <option value="Parent">Parent</option>
      <option value="Admin">Admin</option>
    </select>
  </div>
  
  <script>
  function validateAddress() {
	  const addressInput = document.getElementById("address");
	  const errorSpan = document.getElementById("address-error");
	  const pattern = /^[a-zA-Z0-9 ,./#-]+$/;

	  if (addressInput.value === "") {
	    errorSpan.textContent = "Address is required.";
	  } else if (!pattern.test(addressInput.value)) {
	    errorSpan.textContent = "Invalid address. Avoid using symbols like @, %, $, etc.";
	  } else {
	    errorSpan.textContent = "";
	  }
	}

  </script>

  <div class="form-group">
    <label for="image" style="color: white;">Profile Picture</label>
    <input type="file" id="image" name="imageUrl" accept="image/*">
  </div>
  
  <c:if test="${not empty error}">
  <div class="alert alert-danger">${error}</div>
</c:if>
<c:if test="${not empty success}">
  <div class="alert alert-success">${success}</div>
</c:if>

<c:if test="${not empty sessionScope.success}">
    <div class="alert alert-success">${sessionScope.success}</div>
    <c:remove var="success" scope="session" />
</c:if>

  

  <button type="submit" class="create-account-btn">CREATE ACCOUNT</button>

  <div class="login-link">
    Already have an account? <a href="${pageContext.request.contextPath}/Login">Log in</a>
  </div>
</form>

</div>

  </div>
  </div>
  
  <a href="${pageContext.request.contextPath}/landing" class="back-btn">&lt;</a>
  
</body>
</html>