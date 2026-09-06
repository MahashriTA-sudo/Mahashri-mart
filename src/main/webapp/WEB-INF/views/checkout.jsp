<%@ include file="header.jspf" %>
<c:set var="pageTitle" value="Checkout — MahashriMart"/>
<div class="container page-narrow">
    <div class="page-heading">
        <div>
            <p class="eyebrow">MOCK CHECKOUT</p>
            <h1>One last look.</h1>
        </div>
        <a class="back-link" href="<c:out value='${pageContext.request.contextPath}'/>/cart">&#8592; Back to cart</a>
    </div>
    <c:if test="${not empty error}"><div class="notice notice-error"><c:out value="${error}"/></div></c:if>
    <div class="checkout-layout">
        <section class="checkout-card">
            <div class="mock-payment-badge"><span>✓</span> Secure mock payment</div>
            <h2>Confirm your order</h2>
            <p class="checkout-copy">This MVP uses a mock payment confirmation. Press the button below to place the order and reserve your items.</p>
            <div class="checkout-items">
                <c:forEach var="item" items="${items}">
                    <div class="checkout-item">
                        <span><c:out value="${item.quantity}"/> × <c:out value="${item.productName}"/></span>
                        <strong>&#8377;<fmt:formatNumber value="${item.lineTotal}" minFractionDigits="2"/></strong>
                    </div>
                </c:forEach>
            </div>
            <form method="post" action="<c:out value='${pageContext.request.contextPath}'/>/checkout">
                <button class="button button-full" type="submit">Confirm and place order <span>&#8594;</span></button>
            </form>
        </section>
        <aside class="checkout-note">
            <span class="note-number">01</span>
            <h3>Good to know</h3>
            <p>Your stock is reserved when you confirm. The order will appear in your history as confirmed.</p>
            <span class="note-number">02</span>
            <h3>Made for the MVP</h3>
            <p>No payment gateway is connected yet. This step is intentionally a safe demo.</p>
        </aside>
    </div>
</div>
<%@ include file="footer.jspf" %>