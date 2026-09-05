<%@ include file="header.jspf" %>
<c:set var="pageTitle" value="MahashriMart — thoughtful goods from small sellers"/>
<section class="hero">
    <div class="container hero-grid">
        <div>
            <p class="eyebrow">THE EVERYDAY, MADE SPECIAL</p>
            <h1>Good things, from people who care.</h1>
            <p class="hero-copy">Discover pantry staples, homeware, and small rituals from independent sellers across India.</p>
            <div class="hero-actions">
                <a class="button" href="#marketplace">Explore the marketplace</a>
                <a class="text-link" href="<c:out value='${pageContext.request.contextPath}'/>/register">Become a seller <span>&#8594;</span></a>
            </div>
        </div>
        <div class="hero-art">
            <div class="sun-disc"></div>
            <div class="hero-card hero-card-back"></div>
            <div class="hero-card hero-card-front">
                <span class="hero-card-label">CURATED TODAY</span>
                <strong>Objects with a story.</strong>
                <span>From local hands to your home.</span>
            </div>
        </div>
    </div>
</section>
<section id="marketplace" class="container section">
    <div class="section-heading">
        <div>
            <p class="eyebrow">THE MARKETPLACE</p>
            <h2>Meet your next everyday favorite.</h2>
        </div>
        <span class="result-count"><c:out value="${products.size()}"/> finds</span>
    </div>
    <div class="product-grid">
        <c:forEach var="product" items="${products}">
            <article class="product-card">
                <a class="product-image-wrap" href="<c:out value='${pageContext.request.contextPath}'/>/product?id=<c:out value='${product.id}'/>">
                    <c:choose>
                        <c:when test="${not empty product.imageUrl}">
                            <img class="product-image" src="<c:out value='${product.imageUrl}'/>" alt="<c:out value='${product.name}'/>">
                        </c:when>
                        <c:otherwise><div class="image-placeholder"><span>Mahashri</span></div></c:otherwise>
                    </c:choose>
                    <span class="category-pill"><c:out value="${product.category}"/></span>
                </a>
                <div class="product-info">
                    <div class="product-meta">
                        <h3><a href="<c:out value='${pageContext.request.contextPath}'/>/product?id=<c:out value='${product.id}'/>"><c:out value="${product.name}"/></a></h3>
                        <strong>&#8377;<fmt:formatNumber value="${product.price}" minFractionDigits="2"/></strong>
                    </div>
                    <p><c:out value="${product.description}"/></p>
                    <span class="seller-line">by <c:out value="${product.sellerName}"/></span>
                </div>
            </article>
        </c:forEach>
    </div>
</section>
<section class="seller-callout">
    <div class="container seller-callout-inner">
        <div>
            <p class="eyebrow">FOR INDEPENDENT MAKERS</p>
            <h2>Your craft deserves a place here.</h2>
            <p>Share what you make with people looking for more considered ways to live.</p>
        </div>
        <a class="button button-light" href="<c:out value='${pageContext.request.contextPath}'/>/register">Start selling <span>&#8594;</span></a>
    </div>
</section>
<%@ include file="footer.jspf" %>