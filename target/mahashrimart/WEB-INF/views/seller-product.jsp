<%@ include file="header.jspf" %>
<c:set var="pageTitle" value="List a product — MahashriMart"/>
<section class="auth-page seller-page">
    <div class="auth-card auth-card-wide">
        <p class="eyebrow">SELL ON MAHASHRIMART</p>
        <h1>Put your work in good company.</h1>
        <p class="auth-intro">Tell shoppers about the piece, pantry staple, or ritual you are proud to make.</p>
        <c:if test="${not empty error}"><div class="notice notice-error"><c:out value="${error}"/></div></c:if>
        <form method="post" action="<c:out value='${pageContext.request.contextPath}'/>/seller/products/new" class="form-stack">
            <label for="name">Product name</label>
            <input id="name" name="name" type="text" placeholder="e.g. Hand-thrown breakfast bowl" required>
            <label for="description">Description</label>
            <textarea id="description" name="description" rows="5" placeholder="What makes it special?" required></textarea>
            <div class="form-row">
                <div>
                    <label for="price">Price (&#8377;)</label>
                    <input id="price" name="price" type="number" min="0" step="0.01" placeholder="0.00" required>
                </div>
                <div>
                    <label for="stockQty">Stock quantity</label>
                    <input id="stockQty" name="stockQty" type="number" min="0" step="1" placeholder="0" required>
                </div>
            </div>
            <label for="category">Category</label>
            <input id="category" name="category" type="text" placeholder="e.g. Home, Pantry, Wellness" required>
            <label for="imageUrl">Image URL <span class="label-hint">optional</span></label>
            <input id="imageUrl" name="imageUrl" type="url" placeholder="https://...">
            <button class="button button-full" type="submit">Publish listing <span>&#8594;</span></button>
        </form>
    </div>
</section>
<%@ include file="footer.jspf" %>