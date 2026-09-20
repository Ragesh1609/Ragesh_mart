<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Orders — RageshMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<jsp:include page="/WEB-INF/jsp/_nav.jsp" />
<main class="container">
    <h1>Order History</h1>
    <div class="tabs">
        <button id="tabBuyer" class="tab active">My Orders</button>
        <button id="tabSeller" class="tab">Incoming (Seller)</button>
    </div>
    <div id="ordersList"></div>
</main>
<script src="${pageContext.request.contextPath}/js/main.js"></script>
<script src="${pageContext.request.contextPath}/js/orders.js"></script>
</body>
</html>
