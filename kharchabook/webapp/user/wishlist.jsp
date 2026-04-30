<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Wishlist" scope="request"/>
<jsp:include page="/includes/header.jsp"/>

<div class="page-hero">
    <div>
        <h1 class="page-title">Saved tips</h1>
        <p class="lead">Keep the tips you want to read again in one place.</p>
    </div>
    <div class="hero-actions">
        <a href="${pageContext.request.contextPath}/user/tips" class="btn btn-secondary">Browse all tips</a>
    </div>
</div>

<jsp:include page="/includes/flash.jsp"/>

<c:forEach var="t" items="${tips}">
    <article class="content tip-card">
        <h2 style="margin-top:0;font-size:1.15rem">${t.title}</h2>
        <p class="small-muted">${t.category}</p>
        <p>${t.content}</p>
        <form method="post" action="${pageContext.request.contextPath}/user/wishlist" onsubmit="return confirm('Remove from wishlist?');">
            <input type="hidden" name="action" value="remove">
            <input type="hidden" name="tipId" value="${t.id}">
            <button type="submit" class="btn btn-danger btn-sm">Remove</button>
        </form>
    </article>
</c:forEach>

<c:if test="${empty tips}">
    <div class="soft-panel">
        <p class="small-muted">Your wishlist is empty. <a href="${pageContext.request.contextPath}/user/tips">Browse tips</a></p>
    </div>
</c:if>

<jsp:include page="/includes/footer.jsp"/>
