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
            <div class="password-wrap">
                <input id="password" name="password" type="password" autocomplete="current-password" required>
                <button type="button" id="togglePassword" class="password-toggle" aria-label="Show password" aria-pressed="false">Show</button>
            </div>
            <button class="button button-full" type="submit">Log in <span>&#8594;</span></button>
        </form>
        <p class="form-footnote">New here? <a class="text-link" href="<c:out value='${pageContext.request.contextPath}'/>/register">Create an account</a></p>

        <div class="demo-box">
            <p class="demo-title">Demo accounts</p>
            <ul class="demo-list">
                <li><span>Buyer</span> buyer1@mahashri.com</li>
                <li><span>Seller</span> seller1@mahashri.com</li>
                <li><span>Admin</span> admin@mahashri.com</li>
            </ul>
            <p class="demo-note">Password for all: <strong>password</strong></p>
        </div>
    </div>
</section>
<script>
    (function () {
        var input = document.getElementById('password');
        var btn = document.getElementById('togglePassword');
        if (!input || !btn) { return; }
        btn.addEventListener('click', function () {
            var show = input.type === 'password';
            input.type = show ? 'text' : 'password';
            btn.textContent = show ? 'Hide' : 'Show';
            btn.setAttribute('aria-label', show ? 'Hide password' : 'Show password');
            btn.setAttribute('aria-pressed', show ? 'true' : 'false');
        });
    })();
</script>
<%@ include file="footer.jspf" %>