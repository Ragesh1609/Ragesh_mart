<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Cart — RageshMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<jsp:include page="/WEB-INF/jsp/_nav.jsp" />
<main class="container">
    <h1>Your Cart</h1>
    <div id="cartList"></div>
    <div id="cartTotal" class="cart-total"></div>
    <button id="checkoutBtn" class="primary-btn">Proceed to Mock Checkout</button>
</main>

<div id="checkoutModal" class="modal hidden">
    <div class="modal-content">
        <h2>Mock Payment Confirmation</h2>
        <p>This simulates a payment step. No real card is charged (per project scope constraints).</p>
        <button id="confirmPaymentBtn" class="primary-btn">Confirm Payment</button>
        <button id="cancelPaymentBtn">Cancel</button>
    </div>
</div>

<script src="${pageContext.request.contextPath}/js/main.js"></script>
<script src="${pageContext.request.contextPath}/js/cart.js"></script>
</body>
</html>
