<%@ include file="header.jspf" %>
<c:set var="pageTitle" value="${product.name} — MahashriMart"/>
<div class="container detail-page">
    <c:if test="${not empty success}"><div class="notice notice-success"><c:out value="${success}"/></div></c:if>
    <c:if test="${not empty error}"><div class="notice notice-error"><c:out value="${error}"/></div></c:if>
    <a class="back-link" href="<c:out value='${pageContext.request.contextPath}'/>/">&#8592; Back to marketplace</a>
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
            <c:if test="${averageRating > 0}">
                <p class="rating-summary">&#9733; <fmt:formatNumber value="${averageRating}" maxFractionDigits="1"/> average (<c:out value="${reviews.size()}"/> review<c:if test="${reviews.size() != 1}">s</c:if>)</p>
            </c:if>
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

    <div class="reviews-section">
        <h2>Reviews</h2>
        <c:choose>
            <c:when test="${empty reviews}">
                <p>No reviews yet. Be the first to share your thoughts.</p>
            </c:when>
            <c:otherwise>
                <c:forEach var="r" items="${reviews}">
                    <div class="review-item">
                        <p class="review-stars">
                            <c:forEach begin="1" end="5" var="i">
                                <c:choose>
                                    <c:when test="${i <= r.rating}">&#9733;</c:when>
                                    <c:otherwise>&#9734;</c:otherwise>
                                </c:choose>
                            </c:forEach>
                            <strong>&nbsp;<c:out value="${r.userName}"/></strong>
                        </p>
                        <c:if test="${not empty r.comment}"><p><c:out value="${r.comment}"/></p></c:if>
                    </div>
                </c:forEach>
            </c:otherwise>
        </c:choose>

        <c:if test="${sessionScope.user.role == 'BUYER'}">
            <h3>Write a review</h3>
            <form method="post" action="<c:out value='${pageContext.request.contextPath}'/>/reviews/new" class="form-stack">
                <input type="hidden" name="productId" value="<c:out value='${product.id}'/>">
                <label for="rating">Rating (1-5)</label>
                <input id="rating" name="rating" type="number" min="1" max="5" required>
                <label for="comment">Comment <span class="label-hint">optional</span></label>
                <textarea id="comment" name="comment" rows="3" placeholder="Share your experience"></textarea>
                <button class="button" type="submit">Submit review <span>&#8594;</span></button>
            </form>
        </c:if>
    </div>
</div>
<%@ include file="footer.jspf" %>