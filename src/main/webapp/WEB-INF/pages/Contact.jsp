
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CSS/Header.css">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CSS/Footer.css">
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/CSS/Contact.css">

<meta charset="UTF-8">
<title>Contact Us</title>


</head>
<body>
<jsp:include page="Header.jsp"/>

<!-- Top banner section with image -->
<div class="top-banner">
    <img src="${pageContext.request.contextPath}/resources/images/Contact/kissesbanner.png" alt="Banner Image">

</div>

<!-- Middle banner with "WORK ON YOU, FOR YOU" text -->
<div class="middle-banner">
    <div class="work-text">WORK ON YOU, FOR YOU.</div>
</div>

<!-- Customer Service Form -->
<div class="contact-container">
    <h1>Customer Service</h1>
    <p>Kindly complete the form below using the email address associated with your order.<br>
    This will enable us to assist you more efficiently.</p>

    <form action="submitContact" method="post">
        <div class="form-group">
            <label for="enquiry">Type of Inquiry</label>
            <select id="enquiry" name="enquiry" required>
                <option value="">-- Please Select --</option>
                <option value="order">Order-Related Issue</option>
                <option value="product">Product Information</option>
                <option value="general">General Inquiry</option>
            </select>
        </div>
        
        <div class="name-fields">
            <div class="form-group">
                <label for="firstName">First Name</label>
                <input type="text" id="firstName" name="firstName" placeholder="Enter your first name" required>
            </div>
            <div class="form-group">
                <label for="lastName">Last Name</label>
                <input type="text" id="lastName" name="lastName" placeholder="Enter your last name" required>
            </div>
        </div>

        <div class="form-group">
            <label for="email">Email Address</label>
            <input type="email" id="email" name="email" placeholder="Enter your email address" required>
        </div>
        
        <div class="form-group">
            <label for="message">Your Message</label>
            <textarea id="message" name="message" placeholder="Please provide detailed information about your inquiry..." required></textarea>
        </div>

        <button type="submit">Submit Inquiry</button>
        <p id="thank-you-message">Thank you for your response! We will get back to you soon!</p>
        

        <p class="note">*Our team responds to messages in the order they are received. We appreciate your patience.</p>
    </form>
</div>

<script>
document.querySelector('form').addEventListener('submit', function(event) {
    event.preventDefault(); // prevent actual submission for frontend effect
    document.getElementById('thank-you-message').style.display = 'block';
    this.reset(); // clear the form
});
</script>


<jsp:include page="Footer.jsp"/>
</body>
</html>