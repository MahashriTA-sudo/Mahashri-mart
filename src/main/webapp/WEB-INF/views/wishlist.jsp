<%@ include file="header.jspf" %>
<c:set var="pageTitle" value="Your wishlist — MahashriMart"/>
<div class="container page-narrow">
    <div class="page-heading">
        <div>
            <p class="eyebrow">YOUR WISHLIST</p>
            <h1>Saved for later.</h1>
        </div>
        <a class="back-link" href="<c:out value='${pageContext.request.contextPath}'/>/">&#8592; Continue shopping</a>
    </div>
    <c:if test="${not empty success}"><div class="notice notice-success"><c:out value="${success}"/></div></c:if>
    <c:if test="${not empty error}"><div class="notice notice-error"><c:out value="${error}"/></div></c:if>
    <c:choose>
        <c:when test="${empty items}">
            <div class="empty-state">
                <div class="empty-mark">&#9825;</div>
                <h2>Nothing saved yet.</h2>
                <p>Tap the heart on any product to keep it here for later.</p>
                <a class="button" href="<c:out value='${pageContext.request.contextPath}'/>/">Browse goods <span>&#8594;</span></a>
            </div>
        </c:when>
        <c:otherwise>
            <div class="cart-items">
                <c:forEach var="item" items="${items}">
                    <article class="cart-item wishlist-item">
                        <c:choose>
                            <c:when test="${not empty item.imageUrl}"><img src="<c:out value='${item.imageUrl}'/>" alt="<c:out value='${item.productName}'/>"></c:when>
                            <c:otherwise><div class="cart-image-placeholder">M</div></c:otherwise>
                        </c:choose>
                        <div class="cart-item-info">
                            <h2><a href="<c:out value='${pageContext.request.contextPath}'/>/product?id=<c:out value='${item.productId}'/>"><c:out value="${item.productName}"/></a></h2>
                            <span><c:out value="${item.category}"/></span>
                            <span>&#8377;<fmt:formatNumber value="${item.unitPrice}" minFractionDigits="2"/></span>
                        </div>
                        <div class="wishlist-actions">
                            <c:choose>
                                <c:when test="${item.availableStock > 0}">
                                    <form method="post" action="<c:out value='${pageContext.request.contextPath}'/>/cart/add">
                                        <input type="hidden" name="productId" value="<c:out value='${item.productId}'/>">
                                        <input type="hidden" name="quantity" value="1">
                                        <button class="button button-small" type="submit">Add to cart <span>&#8594;</span></button>
                                    </form>
                                </c:when>
                                <c:otherwise><span class="out-of-stock">Out of stock</span></c:otherwise>
                            </c:choose>
                            <form method="post" action="<c:out value='${pageContext.request.contextPath}'/>/wishlist/remove">
                                <input type="hidden" name="productId" value="<c:out value='${item.productId}'/>">
                                <button class="remove-button" type="submit">Remove</button>
                            </form>
                        </div>
                    </article>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</div>
<%@ include file="footer.jspf" %>
