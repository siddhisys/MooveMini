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
/* Inline style fix for the buttons */
.action-buttons-container {
    display: flex;
    justify-content: center;
    gap: 20px;
    margin-top: 20px;
}

.profile-form, .delete-form {
    display: inline;
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
        <c:when test="${not empty user.image_path and user.image_path != ''}">
            <img src="${pageContext.request.contextPath}/resources/images/user/${user.image_path}" 
                 alt="Profile Picture" class="profile-pic"
                 onerror="this.onerror=null; this.src='${pageContext.request.contextPath}/resources/images/user/default.jpeg';">
        </c:when>
        <c:otherwise>
            <img src="${pageContext.request.contextPath}/resources/images/user/default.jpeg" 
                 alt="Default Profile Picture" class="profile-pic">
        </c:otherwise>
    </c:choose>

    <h3>Personal Information</h3>

    <!-- Profile Update Form -->
    <form method="POST" action="${pageContext.request.contextPath}/profile" enctype="multipart/form-data" class="profile-form">
        <input type="hidden" name="csrfToken" value="${csrfToken}">
        
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
                <input type="password" name="password" placeholder="Enter new password (leave blank to keep current)">
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

        <!-- Action buttons container to group the buttons together visually -->
        <div class="action-buttons-container">
            <button type="submit" class="edit-btn">Save Changes</button>
        </div>
    </form>
    
    <!-- Separate Delete Form - Now part of the button container for visual alignment -->
    <form method="POST" action="${pageContext.request.contextPath}/profile/delete" 
          onsubmit="return confirm('Are you sure you want to delete your account? This action cannot be undone.');" 
          class="delete-form">
        <input type="hidden" name="csrfToken" value="${csrfToken}">
        <button type="submit" class="delete-btn">Delete Account</button>
    </form>
        </div><!-- Close the action-buttons-container div -->

    <!-- Enrollment Section at the Bottom -->
    <div class="enrollment-container">
        <h3>Class Enrollments</h3>
        <div class="enrollment-wrapper">
            <div class="enrollment-list">
                <c:choose>
                    <c:when test="${not empty userEnrollments and not empty userEnrollments}">
                        <c:forEach var="enrollment" items="${userEnrollments}">
                            <div class="enrollment-item">
                                <div class="enrollment-details">
                                    <strong>${enrollment.program_Name}</strong><br>
                                    Time: 6:00 PM - 7:00 PM<br>
                                    Venue: Moove Dance Studio<br>
                                    Duration: 1 Hour
                                </div>
                                <button type="button" class="view-details-btn" onclick="showDetails('${enrollment.program_Name}')">View Details</button>
                            </div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <div class="enrollment-item">You aren't grooving into any mooves!</div>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>


<!-- Modal -->
<div id="modal" class="modal">
    <div class="modal-content">
        <span class="modal-close" onclick="closeModal()">×</span>
        <div class="modal-header" id="modalProgramName"></div>
        <div class="modal-details">
            Time: 6:00 PM - 7:00 PM<br>
            Venue: Moove Dance Studio<br>
            Duration: 1 Hour
        </div>
        <button class="modal-button" onclick="closeModal()">Close</button>
    </div>
</div>

<jsp:include page="/WEB-INF/pages/Footer.jsp"/>
<script>
    function showEnrollmentPopup() {
        alert("Let's start mooving now! Check your profile for details!");
    }

    function showDetails(programName) {
        document.getElementById('modalProgramName').textContent = programName;
        document.getElementById('modal').style.display = 'block';
    }

    function closeModal() {
        document.getElementById('modal').style.display = 'none';
    }

    window.onload = function() {
        <c:if test="${not empty successEnrollmentMessage}">
            showEnrollmentPopup();
        </c:if>
    };
</script>
</body>
</html>