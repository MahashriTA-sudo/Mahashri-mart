<%@ include file="header.jspf" %>
<c:set var="pageTitle" value="Your cart — MahashriMart"/>
<div class="container page-narrow">
    <div class="page-heading">
        <div>
            <p class="eyebrow">YOUR BAG</p>
            <h1>A few good things.</h1>
        </div>
        <a class="back-link" href="<c:out value='${pageContext.request.contextPath}'/>/">← Continue shopping</a>
    </div>
    <c:if test="${not empty error}"><div class="notice notice-error"><c:out value="${error}"/></div></c:if>
    <c:choose>
        <c:when test="${empty items}">
            <div class="empty-state">
                <div class="empty-mark">✦</div>
                <h2>Your cart is waiting for its first find.</h2>
                <p>Explore the marketplace and bring something thoughtful home.</p>
                <a class="button" href="<c:out value='${pageContext.request.contextPath}'/>/">Browse goods <span>&#8594;</span></a>
            </div>
        </c:when>
        <c:otherwise>
            <div class="cart-layout">
                <div class="cart-items">
                    <c:forEach var="item" items="${items}">
                        <article class="cart-item">
                            <c:choose>
                                <c:when test="${not empty item.imageUrl}"><img src="<c:out value='${item.imageUrl}'/>" alt="<c:out value='${item.productName}'/>"></c:when>
                                <c:otherwise><div class="cart-image-placeholder">M</div></c:otherwise>
                            </c:choose>
                            <div class="cart-item-info">
                                <h2><c:out value="${item.productName}"/></h2>
                                <span>&#8377;<fmt:formatNumber value="${item.unitPrice}" minFractionDigits="2"/> each</span>
                                <form class="quantity-form" method="post" action="<c:out value='${pageContext.request.contextPath}'/>/cart/update">
                                    <input type="hidden" name="productId" value="<c:out value='${item.productId}'/>">
                                    <label for="qty-<c:out value='${item.productId}'/>">Qty</label>
                                    <input id="qty-<c:out value='${item.productId}'/>" name="quantity" type="number" min="1" max="<c:out value='${item.availableStock}'/>" value="<c:out value='${item.quantity}'/>">
                                    <button class="subtle-button" type="submit">Update</button>
                                </form>
                            </div>
                            <div class="cart-item-total">
                                <strong>&#8377;<fmt:formatNumber value="${item.lineTotal}" minFractionDigits="2"/></strong>
                                <form method="post" action="<c:out value='${pageContext.request.contextPath}'/>/cart/remove">
                                    <input type="hidden" name="productId" value="<c:out value='${item.productId}'/>">
                                    <button class="remove-button" type="submit">Remove</button>
                                </form>
                            </div>
                        </article>
                    </c:forEach>
                </div>
                <aside class="summary-card">
                    <p class="eyebrow">ORDER SUMMARY</p>
                    <div class="summary-row"><span>Subtotal</span><strong>&#8377;<fmt:formatNumber value="${total}" minFractionDigits="2"/></strong></div>
                    <div class="summary-row muted"><span>Shipping</span><span>Calculated at checkout</span></div>
                    <div class="summary-divider"></div>
                    <div class="summary-row total-row"><span>Total</span><strong>&#8377;<fmt:formatNumber value="${total}" minFractionDigits="2"/></strong></div>
                    <a class="button button-full" href="<c:out value='${pageContext.request.contextPath}'/>/checkout">Review checkout <span>&#8594;</span></a>
                    <small>Mock payment confirmation only. No real payment is collected.</small>
                </aside>
            </div>
        </c:otherwise>
    </c:choose>
</div>
<%@ include file="footer.jspf" %>