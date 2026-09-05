<%@ include file="header.jspf" %>
<c:set var="pageTitle" value="My listings — MahashriMart"/>
<section class="auth-page seller-page">
    <div class="auth-card auth-card-wide">
        <p class="eyebrow">SELL ON MAHASHRIMART</p>
        <h1>Your listings.</h1>
        <p class="auth-intro">Manage the products you have published.</p>
        <p><a href="<c:out value='${pageContext.request.contextPath}'/>/seller/products/new" class="button">Add new listing <span>&#8594;</span></a></p>
        <c:choose>
            <c:when test="${empty myProducts}">
                <p>You have not listed anything yet.</p>
            </c:when>
            <c:otherwise>
                <table class="listings-table">
                    <thead>
                        <tr>
                            <th>Name</th>
                            <th>Category</th>
                            <th>Price</th>
                            <th>Stock</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="p" items="${myProducts}">
                            <tr>
                                <td><c:out value="${p.name}"/></td>
                                <td><c:out value="${p.category}"/></td>
                                <td>&#8377;<c:out value="${p.price}"/></td>
                                <td><c:out value="${p.stockQty}"/></td>
                                <td>
                                    <a href="<c:out value='${pageContext.request.contextPath}'/>/seller/products/edit?id=<c:out value='${p.id}'/>">Edit</a>
                                    &nbsp;|&nbsp;
                                    <form method="post" action="<c:out value='${pageContext.request.contextPath}'/>/seller/products/delete?id=<c:out value='${p.id}'/>" style="display:inline;">
                                        <button type="submit" class="link-button" onclick="return confirm('Delete this listing?');">Delete</button>
                                    </form>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </c:otherwise>
        </c:choose>
    </div>
</section>
<%@ include file="footer.jspf" %>