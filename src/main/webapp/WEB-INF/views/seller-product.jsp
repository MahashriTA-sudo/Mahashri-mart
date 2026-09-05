<%@ include file="header.jspf" %>
<c:set var="pageTitle" value="${editMode ? 'Edit listing' : 'List a product'} — MahashriMart"/>
<section class="auth-page seller-page">
    <div class="auth-card auth-card-wide">
        <p class="eyebrow">SELL ON MAHASHRIMART</p>
        <c:choose>
            <c:when test="${editMode}">
                <h1>Update your listing.</h1>
                <p class="auth-intro">Make changes to the details shoppers see.</p>
            </c:when>
            <c:otherwise>
                <h1>Put your work in good company.</h1>
                <p class="auth-intro">Tell shoppers about the piece, pantry staple, or ritual you are proud to make.</p>
            </c:otherwise>
        </c:choose>
        <c:if test="${not empty error}"><div class="notice notice-error"><c:out value="${error}"/></div></c:if>
        <c:choose>
            <c:when test="${editMode}">
                <form method="post" action="<c:out value='${pageContext.request.contextPath}'/>/seller/products/edit?id=<c:out value='${product.id}'/>" class="form-stack">
            </c:when>
            <c:otherwise>
                <form method="post" action="<c:out value='${pageContext.request.contextPath}'/>/seller/products/new" class="form-stack">
            </c:otherwise>
        </c:choose>
            <label for="name">Product name</label>
            <input id="name" name="name" type="text" placeholder="e.g. Hand-thrown breakfast bowl"
                   value="<c:out value='${product.name}'/>" required>
            <label for="description">Description</label>
            <textarea id="description" name="description" rows="5" placeholder="What makes it special?" required><c:out value="${product.description}"/></textarea>
            <div class="form-row">
                <div>
                    <label for="price">Price (&#8377;)</label>
                    <input id="price" name="price" type="number" min="0" step="0.01" placeholder="0.00"
                           value="<c:out value='${product.price}'/>" required>
                </div>
                <div>
                    <label for="stockQty">Stock quantity</label>
                    <input id="stockQty" name="stockQty" type="number" min="0" step="1" placeholder="0"
                           value="<c:out value='${product.stockQty}'/>" required>
                </div>
            </div>
            <label for="category">Category</label>
            <input id="category" name="category" type="text" placeholder="e.g. Home, Pantry, Wellness"
                   value="<c:out value='${product.category}'/>" required>
            <label for="imageUrl">Image URL <span class="label-hint">optional</span></label>
            <input id="imageUrl" name="imageUrl" type="url" placeholder="https://..."
                   value="<c:out value='${product.imageUrl}'/>">
            <c:choose>
                <c:when test="${editMode}">
                    <button class="button button-full" type="submit">Save changes <span>&#8594;</span></button>
                </c:when>
                <c:otherwise>
                    <button class="button button-full" type="submit">Publish listing <span>&#8594;</span></button>
                </c:otherwise>
            </c:choose>
        </form>
    </div>
</section>
<%@ include file="footer.jspf" %>