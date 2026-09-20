<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Register — RageshMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<jsp:include page="/WEB-INF/jsp/_nav.jsp" />
<main class="container narrow">
    <h1>Create an account</h1>
    <form id="registerForm" class="form-card">
        <label>Name <input type="text" id="name" required></label>
        <label>Email <input type="email" id="email" required></label>
        <label>Password <input type="password" id="password" minlength="8" required></label>
        <label>I am a...
            <select id="role">
                <option value="BUYER">Buyer</option>
                <option value="SELLER">Seller</option>
            </select>
        </label>
        <button type="submit">Register</button>
        <p id="registerError" class="form-error"></p>
    </form>
    <p>Already have an account? <a href="${pageContext.request.contextPath}/login.jsp">Login here</a></p>
</main>
<script src="${pageContext.request.contextPath}/js/main.js"></script>
<script src="${pageContext.request.contextPath}/js/auth.js"></script>
</body>
</html>
