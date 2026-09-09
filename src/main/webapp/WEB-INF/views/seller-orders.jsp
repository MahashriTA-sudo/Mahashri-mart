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
                                <h2><c:out value="${order.createdAt}"/></h2>
                            </div>
                            <span class="status-pill"><c:out value="${order.status}"/></span>
                        </div>
                        <div class="order-items">
                            <c:forEach var="item" items="${order.items}">
                                <div class="order-item">
                                    <span><c:out value="${item.productName}"/> &times; <c:out value="${item.quantity}"/></span>
                                    <span>&#8377;<c:out value="${item.lineTotal}"/></span>
                                </div>
                            </c:forEach>
                        </div>
                    </article>
                </c:forEach>
            </div>
        </c:otherwise>
    </c:choose>
</div>
<%@ include file="footer.jspf" %>