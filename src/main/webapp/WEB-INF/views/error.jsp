<%@ include file="header.jspf" %>
<c:set var="pageTitle" value="Something went wrong — MahashriMart"/>
<section class="auth-page">
    <div class="auth-card">
        <p class="eyebrow">A SMALL DETOUR</p>
        <h1>That page wandered off.</h1>
        <p class="auth-intro">Try heading back to the marketplace and starting again.</p>
        <a class="button" href="<c:out value='${pageContext.request.contextPath}'/>/">Back to marketplace <span>&#8594;</span></a>
    </div>
</section>
<%@ include file="footer.jspf" %>