<%@ include file="header.jspf" %>
<c:set var="pageTitle" value="Your orders — MahashriMart"/>
<div class="container page-narrow">
    <div class="page-heading">
        <div>
            <p class="eyebrow">YOUR JOURNEY</p>
            <h1>Order history.</h1>
        </div>
        <a class="button button-small" href="<c:out value='${pageContext.request.contextPath}'/>/products">Shop more <span>&#8594;</span></a>
    </div>
    <c:if test="${not empty success}"><div class="notice notice-success"><c:out value="${success}"/></div></c:if>
    <c:if test="${not empty error}"><div class="notice notice-error"><c:out value="${error}"/></div></c:if>
    <c:choose>
        <c:when test="${empty orders}">
            <div class="empty-state">
                <div class="empty-mark">&#9679;</div>
                <h2>Your first order will live here.</h2>
                <p>There are plenty of thoughtful goods waiting to be discovered.</p>
                <a class="button" href="<c:out value='${pageContext.request.contextPath}'/>/products">Explore marketplace</a>
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
                        <c:choose>
                            <c:when test="${order.status == 'CANCELLED'}">
                                <div class="timeline-cancelled">This order was cancelled.</div>
                            </c:when>
                            <c:otherwise>
                                <ol class="timeline">
                                    <li class="timeline-step ${order.progressStep >= 1 ? 'is-done' : ''}">
                                        <span class="timeline-dot"></span>Confirmed
                                    </li>
                                    <li class="timeline-step ${order.progressStep >= 2 ? 'is-done' : ''}">
                                        <span class="timeline-dot"></span>Shipped
                                    </li>
                                    <li class="timeline-step ${order.progressStep >= 3 ? 'is-done' : ''}">
                                        <span class="timeline-dot"></span>Delivered
                                    </li>
                                </ol>
                            </c:otherwise>
                        </c:choose>
                        <div class="order-items">
                            <c:forEach var="item" items="${order.items}">
                                <div class="order-item">
                                    <span><c:out value="${item.productName}"/> &times; <c:out value="${item.quantity}"/></span>
                                    <span>&#8377;<c:out value="${item.unitPrice}"/></span>
                                </div>
                            </c:forEach>
                        </div>
                        <div class="order-total"><span>Total</span><strong>&#8377;<c:out value="${order.totalAmount}"/></strong></div>
                        <c:if test="${order.status == 'PENDING' || order.status == 'CONFIRMED'}">
                            <div class="order-actions">
                                <form method="post" action="<c:out value='${pageContext.request.contextPath}'/>/orders/cancel"
                                      onsubmit="return confirm('Cancel order #${order.id}? Stock will be restored.');">
                                    <input type="hidden" name="id" value="<c:out value='${order.id}'/>"/>
                                    <button type="submit" class="button button-small button-danger">Cancel order</button>
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