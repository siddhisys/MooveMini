<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.moove.model.UsersModel" %>
<%@ page session="true" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>User Profile</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CSS/Header.css">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CSS/Footer.css">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CSS/UserProfile.css">
<style>
    .profile-container {
        max-width: 800px;
        margin: 20px auto;
        padding: 20px;
        background-color: #fff;
        border-radius: 8px;
        box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
    }

    .profile-pic {
        width: 150px;
        height: 150px;
        border-radius: 50%;
        object-fit: cover;
        margin: 0 auto 20px;
        display: block;
        border: 3px solid #3498db;
    }

    .success-message {
        background-color: #d4edda;
        color: #155724;
        padding: 10px;
        border-radius: 5px;
        margin-bottom: 15px;
    }

    .error-message {
        background-color: #f8d7da;
        color: #721c24;
        padding: 10px;
        border-radius: 5px;
        margin-bottom: 15px;
    }

    .image-upload-container {
        margin: 20px 0;
        text-align: center;
    }

    .image-upload-label {
        display: block;
        margin-bottom: 10px;
    }

    .form-row {
        display: flex;
        gap: 20px;
        margin-bottom: 15px;
    }

    .form-group {
        flex: 1;
    }

    .form-group label {
        display: block;
        margin-bottom: 5px;
        font-weight: 600;
    }

    .form-group input {
        width: 100%;
        padding: 8px;
        border: 1px solid #ddd;
        border-radius: 4px;
        background-color: #f5f5f0;
        font-size: 14px;
        box-sizing: border-box;
    }

    .edit-btn {
        background-color: #333;
        color: #fff;
        border: none;
        padding: 10px 20px;
        border-radius: 25px;
        cursor: pointer;
        font-size: 16px;
        margin-top: 20px;
        margin-right: 10px; /* Add spacing between buttons */
    }

    .edit-btn:hover {
        background-color: #555;
    }

    .delete-btn {
        background-color: #dc3545; /* Red color for delete action */
        color: #fff;
        border: none;
        padding: 10px 20px;
        border-radius: 25px;
        cursor: pointer;
        font-size: 16px;
        margin-top: 20px;
    }

    .delete-btn:hover {
        background-color: #c82333; /* Darker red on hover */
    }
</style>
</head>
<body>
<jsp:include page="/WEB-INF/pages/Header.jsp"/>

<div class="profile-container">
    <h2>User Profile</h2>
    
    <c:if test="${not empty successMessage}">
        <div class="success-message">${successMessage}</div>
    </c:if>
    <c:if test="${not empty errorMessage}">
        <div class="error-message">${errorMessage}</div>
    </c:if>

    <c:choose>
        <c:when test="${not empty user.image_path and not empty user.image_path}">
            <img src="${pageContext.request.contextPath}/resources/images/user/${user.image_path}" 
                 alt="Profile Picture" class="profile-pic"
                 onerror="this.onerror=null; this.src='${pageContext.request.contextPath}/resources/images/user/default_avatar.png';">
        </c:when>
        <c:otherwise>
            <img src="${pageContext.request.contextPath}/resources/images/user/default_avatar.png" 
                 alt="Default Profile Picture" class="profile-pic">
        </c:otherwise>
    </c:choose>

    <h3>Personal Information</h3>

    <form method="POST" action="${pageContext.request.contextPath}/profile" enctype="multipart/form-data">
        <div class="image-upload-container">
            <label for="profileImage" class="image-upload-label">Change Profile Picture:</label>
            <input type="file" id="profileImage" name="profileImage" accept="image/*">
        </div>
    
        <div class="form-row">
            <div class="form-group">
                <label>Username</label>
                <input type="text" name="firstName" value="${user.user_Name}" required>
            </div>
            <div class="form-group">
                <label>Password</label>
                <input type="password" name="password" value="${user.password}" required placeholder="Enter new password to change">
            </div>
        </div>

        <div class="form-row">
            <div class="form-group">
                <label>Email</label>
                <input type="email" name="email" value="${user.user_Email}" required>
            </div>
            <div class="form-group">
                <label>Address</label>
                <input type="text" name="address" value="${user.user_Address}">
            </div>
        </div>

        <button type="submit" class="edit-btn">Save Changes</button>
    </form>

    <!-- Separate form for delete action -->
    <form method="POST" action="${pageContext.request.contextPath}/profile/delete" onsubmit="return confirm('Are you sure you want to delete your account? This action cannot be undone.');">
        <button type="submit" class="delete-btn">Delete</button>
    </form>
</div>

<jsp:include page="/WEB-INF/pages/Footer.jsp"/>
</body>
</html>