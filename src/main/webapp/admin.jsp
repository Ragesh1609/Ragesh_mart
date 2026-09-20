<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Admin — RageshMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<jsp:include page="/WEB-INF/jsp/_nav.jsp" />
<main class="container">
    <h1>Admin Panel</h1>
    <div class="tabs">
        <button id="tabUsers" class="tab active">Users</button>
        <button id="tabOrders" class="tab">All Orders</button>
    </div>
    <div id="adminContent"></div>
</main>
<script src="${pageContext.request.contextPath}/js/main.js"></script>
<script src="${pageContext.request.contextPath}/js/admin.js"></script>
</body>
</html>
