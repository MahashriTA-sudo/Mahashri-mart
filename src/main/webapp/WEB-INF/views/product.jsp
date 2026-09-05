<%@ include file="header.jspf" %>
<c:set var="pageTitle" value="${product.name} — MahashriMart"/>
<div class="container detail-page">
    <c:if test="${not empty success}"><div class="notice notice-success"><c:out value="${success}"/></div></c:if>
    <c:if test="${not empty error}"><div class="notice notice-error"><c:out value="${error}"/></div></c:if>
    <a class="back-link" href="<c:out value='${pageContext.request.contextPath}'/>/">← Back to marketplace</a>
    <div class="detail-grid">
        <div class="detail-image">
            <c:choose>
                <c:when test="${not empty product.imageUrl}"><img src="<c:out value='${product.imageUrl}'/>" alt="<c:out value='${product.name}'/>"></c:when>
                <c:otherwise><div class="image-placeholder image-placeholder-large"><span>Mahashri</span></div></c:otherwise>
            </c:choose>
        </div>
        <div class="detail-copy">
            <span class="eyebrow"><c:out value="${product.category}"/></span>
            <h1><c:out value="${product.name}"/></h1>
            <p class="detail-price">&#8377;<fmt:formatNumber value="${product.price}" minFractionDigits="2"/></p>
            <p class="detail-description"><c:out value="${product.description}"/></p>
            <div class="seller-badge">
                <span class="avatar"><c:out value="${product.sellerName.substring(0, 1)}"/></span>
                <span>Sold by <strong><c:out value="${product.sellerName}"/></strong></span>
            </div>
            <div class="stock-note"><span class="stock-dot"></span> <c:out value="${product.stockQty}"/> available</div>
            <c:choose>
                <c:when test="${sessionScope.user.role == 'BUYER'}">
                    <form class="add-form" method="post" action="<c:out value='${pageContext.request.contextPath}'/>/cart/add">
                        <input type="hidden" name="productId" value="<c:out value='${product.id}'/>">
                        <label for="quantity">Quantity</label>
                        <div class="add-row">
                            <input id="quantity" name="quantity" type="number" min="1" max="<c:out value='${product.stockQty}'/>" value="1">
                            <button class="button" type="submit">Add to cart <span>&#8594;</span></button>
                        </div>
                    </form>
                </c:when>
                <c:when test="${empty sessionScope.user}">
                    <a class="button" href="<c:out value='${pageContext.request.contextPath}'/>/login">Log in to purchase <span>&#8594;</span></a>
                </c:when>
            </c:choose>
        </div>
    </div>
</div>
<%@ include file="footer.jspf" %>