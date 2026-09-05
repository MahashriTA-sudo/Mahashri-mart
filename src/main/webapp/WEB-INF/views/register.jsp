<%@ include file="header.jspf" %>
<c:set var="pageTitle" value="Join MahashriMart"/>
<section class="auth-page">
    <div class="auth-card auth-card-wide">
        <p class="eyebrow">MAKE YOUR ENTRANCE</p>
        <h1>There&#8217;s room for your story here.</h1>
        <p class="auth-intro">Create a buyer account to discover thoughtful goods, or join as a seller to share what you make.</p>
        <c:if test="${not empty error}"><div class="notice notice-error"><c:out value="${error}"/></div></c:if>
        <form method="post" action="<c:out value='${pageContext.request.contextPath}'/>/register" class="form-stack">
            <label for="name">Your name</label>
            <input id="name" name="name" type="text" autocomplete="name" required>
            <label for="email">Email address</label>
            <input id="email" name="email" type="email" autocomplete="email" required>
            <label for="password">Password <span class="label-hint">8 characters minimum</span></label>
            <input id="password" name="password" type="password" autocomplete="new-password" minlength="8" required>
            <fieldset>
                <legend>How will you use MahashriMart?</legend>
                <div class="role-options">
                    <label class="role-option"><input type="radio" name="role" value="BUYER" checked><span><strong>Shop</strong><small>Discover and order goods</small></span></label>
                    <label class="role-option"><input type="radio" name="role" value="SELLER"><span><strong>Sell</strong><small>List what you make</small></span></label>
                </div>
            </fieldset>
            <button class="button button-full" type="submit">Create account <span>&#8594;</span></button>
        </form>
        <p class="form-footnote">Already have an account? <a class="text-link" href="<c:out value='${pageContext.request.contextPath}'/>/login">Log in</a></p>
    </div>
</section>
<%@ include file="footer.jspf" %>