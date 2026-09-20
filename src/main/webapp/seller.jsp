<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>Seller Dashboard — RageshMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<jsp:include page="/WEB-INF/jsp/_nav.jsp" />
<main class="container">
    <h1>Seller Dashboard</h1>

    <form id="productForm" class="form-card">
        <input type="hidden" id="productId">
        <label>Name <input type="text" id="pName" required></label>
        <label>Description <textarea id="pDescription"></textarea></label>
        <label>Price (₹) <input type="number" step="0.01" id="pPrice" required></label>
        <label>Stock Qty <input type="number" id="pStock" required></label>
        <label>Category <input type="text" id="pCategory"></label>
        <label>Image URL <input type="text" id="pImageUrl"></label>
        <button type="submit">Save Listing</button>
    </form>

    <h2>My Listings</h2>
    <div id="myListings"></div>
</main>
<script src="${pageContext.request.contextPath}/js/main.js"></script>
<script src="${pageContext.request.contextPath}/js/seller.js"></script>
</body>
</html>
