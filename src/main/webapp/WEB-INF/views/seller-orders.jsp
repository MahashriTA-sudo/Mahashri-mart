<%@ include file="header.jspf" %>
<c:set var="pageTitle" value="Incoming orders — MahashriMart"/>
<div class="container page-narrow">
    <div class="page-heading">
        <div>
            <p class="eyebrow">YOUR SALES</p>
            <h1>Incoming orders.</h1>
        </div>
        <a class="button button-small" href="<c:out value='${pageContext.request.contextPath}'/>/seller/products">My listings <span>&#8594;</span></a>
    </div>
    <c:if test="${not empty success}"><div class="notice notice-success"><c:out value="${success}"/></div></c:if>
    <c:if test="${not empty error}"><div class="notice notice-error"><c:out value="${error}"/></div></c:if>

    <div class="sales-summary">
        <div class="sales-box">
            <span>Total orders</span>
            <strong><c:out value="${summary.totalOrders}"/></strong>
        </div>
        <div class="sales-box">
            <span>Active</span>
            <strong><c:out value="${summary.activeOrders}"/></strong>
        </div>
        <div class="sales-box">
            <span>Delivered</span>
            <strong><c:out value="${summary.deliveredOrders}"/></strong>
        </div>
        <div class="sales-box">
            <span>Cancelled</span>
            <strong><c:out value="${summary.cancelledOrders}"/></strong>
        </div>
        <div class="sales-box sales-box--revenue">
            <span>Total revenue</span>
            <strong>&#8377;<c:out value="${summary.totalRevenue}"/></strong>
        </div>
    </div>

    <c:choose>
        <c:when test="${empty orders}">
            <div class="empty-state">
                <div class="empty-mark">&#9679;</div>
                <h2>No orders yet.</h2>
                <p>Orders containing your products will appear here.</p>
            </div>
        </c:when>
        <c:otherwise>
            <div class="order-list">
                <c:forEach var="order" items="${orders}">
                    <article class="order-card">
                        <div class="order-header">
                            <div>
                                <span class="eyebrow">ORDER #<c:out value="${order.id}"/></span>
                                <h2><c:out value="${order.createdAtDisplay}"/></h2>
                            </div>
                            <span class="status-pill status-pill--${order.status}"><c:out value="${order.status}"/></span>
                        </div>
                        <div class="order-items">
                            <c:forEach var="item" items="${order.items}">
                                <div class="order-item">
                                    <span><c:out value="${item.productName}"/> &times; <c:out value="${item.quantity}"/></span>
                                    <span>&#8377;<c:out value="${item.lineTotal}"/></span>
                                </div>
                            </c:forEach>
                        </div>
                        <%-- Seller actions: CONFIRMED → SHIPPED, SHIPPED → DELIVERED --%>
                        <c:if test="${order.status == 'CONFIRMED'}">
                            <div class="order-actions">
                                <form method="post" action="<c:out value='${pageContext.request.contextPath}'/>/seller/orders/update"
                                      onsubmit="return confirm('Mark order #${order.id} as SHIPPED?');">
                                    <input type="hidden" name="id" value="<c:out value='${order.id}'/>"/>
                                    <input type="hidden" name="status" value="SHIPPED"/>
                                    <button type="submit" class="button button-small">Mark as Shipped</button>
                                </form>
                            </div>
                        </c:if>
                        <c:if test="${order.status == 'SHIPPED'}">
                            <div class="order-actions">
                                <form method="post" action="<c:out value='${pageContext.request.contextPath}'/>/seller/orders/update"
                                      onsubmit="return confirm('Mark order #${order.id} as DELIVERED?');">
                                    <input type="hidden" name="id" value="<c:out value='${order.id}'/>"/>
                                    <input type="hidden" name="status" value="DELIVERED"/>
                                    <button type="submit" class="button button-small">Mark as Delivered</button>
                                </form>
                            </div>
                        </c:if>
                    </article>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</div>
<%@ include file="footer.jspf" %>