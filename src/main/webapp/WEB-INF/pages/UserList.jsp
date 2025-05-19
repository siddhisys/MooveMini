<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CSS/Header.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CSS/Footer.css">
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CSS/UserList.css">

    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>User List</title>
</head>
<body>
<jsp:include page="Header.jsp"/>

<div class="user-list-container">
    <h1 class="user-title">User List</h1>

    <c:if test="${not empty userList}">
        <p class="user-count">Total Users: ${userList.size()}</p>
    </c:if>
    <c:if test="${empty userList}">
        <p class="user-count empty">No users found.</p>
    </c:if>

    <div class="table-wrapper">
        <table class="user-table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Username</th>
                    <th>Role</th>
                    <th>Email</th>
                    <th>Gender</th>
                    <th>Status</th>
                    <th>Enrolled Program</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="user" items="${userList}">
                    <tr>
                        <td>${user.user_ID}</td>
                        <td>${user.user_Name}</td>
                        <td>${user.role_Id}</td>
                        <td>${user.user_Email}</td>
                        <td class="gender ${user.user_Gender.toLowerCase()}">${user.user_Gender}</td>
                        <td class="status ${user.user_Status.toLowerCase()}">${user.user_Status}</td>
                        <td class="enrolled-program ${userProgramMap[user.user_ID] == 'None' ? 'none' : ''}">
                            ${userProgramMap[user.user_ID]}
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</div>

<jsp:include page="Footer.jsp"/>
</body>
</html>