<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>RageshMart</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<jsp:include page="/WEB-INF/jsp/_nav.jsp" />

<main class="container">
    <section class="hero">
        <h1>RageshMart</h1>
        <p>Multi-seller marketplace — browse, add to cart, and check out.</p>
    </section>

    <section class="filters">
        <input type="text" id="searchInput" placeholder="Search products...">
        <select id="categoryFilter">
            <option value="">All categories</option>
            <option value="Electronics">Electronics</option>
            <option value="Apparel">Apparel</option>
        </select>
        <button id="searchBtn">Search</button>
    </section>

    <section id="productGrid" class="product-grid">
        <p>Loading products...</p>
    </section>
</main>

<jsp:include page="/WEB-INF/jsp/_chat-widget.jsp" />

<script src="${pageContext.request.contextPath}/js/main.js"></script>
<script src="${pageContext.request.contextPath}/js/products.js"></script>
</body>
</html>
