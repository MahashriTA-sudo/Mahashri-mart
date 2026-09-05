<%@ include file="header.jspf" %>
<c:set var="pageTitle" value="Your orders — MahashriMart"/>
<div class="container page-narrow">
    <div class="page-heading">
        <div>
            <p class="eyebrow">YOUR JOURNEY</p>
            <h1>Order history.</h1>
        </div>
        <a class="button button-small" href="<c:out value='${pageContext.request.contextPath}'/>/">Shop more <span>&#8594;</span></a>
    </div>
    <c:if test="${not empty success}"><div class="notice notice-success"><c:out value="${success}"/></div></c:if>
    <c:choose>
        <c:when test="${empty orders}">
            <div class="empty-state">
                <div class="empty-mark">○</div>
                <h2>Your first order will live here.</h2>
                <p>There are plenty of thoughtful goods waiting to be discovered.</p>
                <a class="button" href="<c:out value='${pageContext.request.contextPath}'/>/">Explore marketplace <span>&#8594;</span></a>
            </div>
        </c:when>
        <c:otherwise>
            <div class="order-list">
                <c:forEach var="order" items="${orders}">
                    <article class="order-card">
                        <div class="order-header">
                            <div>
                                <span class="eyebrow">ORDER #<c:out value="${order.id}"/></span>
                                <h2><c:out value="${order.createdAt}"/></h2>
                            </div>
                            <span class="status-pill"><c:out value="${order.status}"/></span>
                        </div>
                        <div class="order-items">
                            <c:forEach var="item" items="${order.items}">
                                <div class="order-item"><span><c:out value="${item.quantity}"/> × <c:out value="${item.productName}"/></span><strong>&#8377;<fmt:formatNumber value="${item.lineTotal}" minFractionDigits="2"/></strong></div>
                            </c:forEach>
                        </div>
                        <div class="order-total"><span>Total</span><strong>&#8377;<fmt:formatNumber value="${order.totalAmount}" minFractionDigits="2"/></strong></div>
                    </article>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</div>
<%@ include file="footer.jspf" %>