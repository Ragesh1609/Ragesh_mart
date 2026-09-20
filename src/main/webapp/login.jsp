<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Login — RageshMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<jsp:include page="/WEB-INF/jsp/_nav.jsp" />
<main class="container narrow">
    <h1>Login</h1>
    <form id="loginForm" class="form-card">
        <label>Email <input type="email" id="email" required></label>
        <label>Password <input type="password" id="password" required></label>
        <button type="submit">Login</button>
        <p id="loginError" class="form-error"></p>
    </form>
    <p>No account? <a href="${pageContext.request.contextPath}/register.jsp">Register here</a></p>
    <p class="hint">Demo accounts: admin@rageshmart.com / AdminPass123,
        seller@rageshmart.com / SellerPass123, buyer@rageshmart.com / BuyerPass123</p>
</main>
<script src="${pageContext.request.contextPath}/js/main.js"></script>
<script src="${pageContext.request.contextPath}/js/auth.js"></script>
</body>
</html>
