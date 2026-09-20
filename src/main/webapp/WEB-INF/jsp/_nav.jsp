<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<nav class="navbar">
    <a href="${pageContext.request.contextPath}/index.jsp" class="brand">RageshMart</a>
    <div class="nav-links">
        <a href="${pageContext.request.contextPath}/index.jsp">Shop</a>
        <a href="${pageContext.request.contextPath}/cart.jsp">Cart</a>
        <a href="${pageContext.request.contextPath}/orders.jsp">Orders</a>
        <a href="${pageContext.request.contextPath}/seller.jsp">Sell</a>
        <span id="navAuthArea">
            <a href="${pageContext.request.contextPath}/login.jsp" id="navLoginLink">Login</a>
        </span>
    </div>
</nav>
