<%@ include file="header.jspf" %>
<c:set var="pageTitle" value="Admin dashboard — MahashriMart"/>
<section class="auth-page seller-page">
    <div class="auth-card auth-card-wide">
        <p class="eyebrow">ADMIN</p>
        <h1>Store overview.</h1>
        <p class="auth-intro">Manage accounts, orders, and listings across MahashriMart.</p>

        <p class="admin-tabs">
            <a href="<c:out value='${pageContext.request.contextPath}'/>/admin/users"
               class="${activeTab == 'users' ? 'button' : 'button button-outline'}">Users</a>
            &nbsp;
            <a href="<c:out value='${pageContext.request.contextPath}'/>/admin/orders"
               class="${activeTab == 'orders' ? 'button' : 'button button-outline'}">Orders</a>
            &nbsp;
            <a href="<c:out value='${pageContext.request.contextPath}'/>/admin/products"
               class="${activeTab == 'products' ? 'button' : 'button button-outline'}">Products</a>
        </p>

        <c:if test="${not empty success}"><div class="notice notice-success"><c:out value="${success}"/></div></c:if>
        <c:if test="${not empty error}"><div class="notice notice-error"><c:out value="${error}"/></div></c:if>

        <%-- ── USERS TAB ─────────────────────────────────────────────────────── --%>
        <c:if test="${activeTab == 'users'}">
            <table class="listings-table">
                <thead>
                    <tr><th>Name</th><th>Email</th><th>Role</th></tr>
                </thead>
                <tbody>
                    <c:forEach var="u" items="${allUsers}">
                        <tr>
                            <td><c:out value="${u.name}"/></td>
                            <td><c:out value="${u.email}"/></td>
                            <td><c:out value="${u.role}"/></td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:if>

        <%-- ── ORDERS TAB ────────────────────────────────────────────────────── --%>
        <c:if test="${activeTab == 'orders'}">
            <%-- Status filter bar --%>
            <div class="admin-filter-bar">
                <a href="<c:out value='${pageContext.request.contextPath}'/>/admin/orders"
                   class="${empty statusFilter ? 'button button-small' : 'button button-small button-outline'}">All</a>
                <c:forEach var="s" items="${allStatuses}">
                    <a href="<c:out value='${pageContext.request.contextPath}'/>/admin/orders?status=${s}"
                       class="${statusFilter == s ? 'button button-small' : 'button button-small button-outline'}"><c:out value="${s}"/></a>
                </c:forEach>
            </div>
            <table class="listings-table">
                <thead>
                    <tr>
                        <th>Order ID</th>
                        <th>Buyer ID</th>
                        <th>Status</th>
                        <th>Total</th>
                        <th>Update status</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="o" items="${allOrders}">
                        <tr>
                            <td><c:out value="${o.id}"/></td>
                            <td><c:out value="${o.buyerId}"/></td>
                            <td><span class="status-pill status-pill--${o.status}"><c:out value="${o.status}"/></span></td>
                            <td>&#8377;<c:out value="${o.totalAmount}"/></td>
                            <td>
                                <form method="post" action="<c:out value='${pageContext.request.contextPath}'/>/admin/orders/update"
                                      style="display:flex;gap:4px;align-items:center;"
                                      onsubmit="return confirm('Update order #${o.id} status?');">
                                    <input type="hidden" name="id" value="<c:out value='${o.id}'/>"/>
                                    <input type="hidden" name="statusFilter" value="<c:out value='${statusFilter}'/>"/>
                                    <select name="status" class="select-inline">
                                        <c:forEach var="s" items="${allStatuses}">
                                            <option value="${s}" ${o.status == s ? 'selected' : ''}><c:out value="${s}"/></option>
                                        </c:forEach>
                                    </select>
                                    <button type="submit" class="link-button">Apply</button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:if>

        <%-- ── PRODUCTS TAB ──────────────────────────────────────────────────── --%>
        <c:if test="${activeTab == 'products'}">
            <table class="listings-table">
                <thead>
                    <tr><th>Name</th><th>Seller</th><th>Category</th><th>Price</th><th>Actions</th></tr>
                </thead>
                <tbody>
                    <c:forEach var="p" items="${allProducts}">
                        <tr>
                            <td><c:out value="${p.name}"/></td>
                            <td><c:out value="${p.sellerName}"/></td>
                            <td><c:out value="${p.category}"/></td>
                            <td>&#8377;<c:out value="${p.price}"/></td>
                            <td>
                                <form method="post" action="<c:out value='${pageContext.request.contextPath}'/>/admin/products/remove?id=<c:out value='${p.id}'/>" style="display:inline;">
                                    <button type="submit" class="link-button" onclick="return confirm('Remove this listing?');">Remove</button>
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:if>
    </div>
</section>
<%@ include file="footer.jspf" %>