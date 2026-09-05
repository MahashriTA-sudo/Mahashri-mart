<%@ include file="header.jspf" %>
<c:set var="pageTitle" value="Log in — MahashriMart"/>
<section class="auth-page">
    <div class="auth-card">
        <p class="eyebrow">WELCOME BACK</p>
        <h1>Pick up where you left off.</h1>
        <p class="auth-intro">Sign in to shop independent goods or manage your listings.</p>
        <c:if test="${not empty success}"><div class="notice notice-success"><c:out value="${success}"/></div></c:if>
        <c:if test="${not empty error}"><div class="notice notice-error"><c:out value="${error}"/></div></c:if>
        <form method="post" action="<c:out value='${pageContext.request.contextPath}'/>/login" class="form-stack">
            <label for="email">Email address</label>
            <input id="email" name="email" type="email" autocomplete="email" required>
            <label for="password">Password</label>
            <input id="password" name="password" type="password" autocomplete="current-password" required>
            <button class="button button-full" type="submit">Log in <span>&#8594;</span></button>
        </form>
        <p class="form-footnote">New here? <a class="text-link" href="<c:out value='${pageContext.request.contextPath}'/>/register">Create an account</a></p>
    </div>
</section>
<%@ include file="footer.jspf" %>