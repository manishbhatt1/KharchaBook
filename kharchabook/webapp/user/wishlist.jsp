<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- Set page title -->
<c:set var="pageTitle" value="Wishlist" scope="request"/>

<!-- Include common header -->
<jsp:include page="/includes/header.jsp"/>

<!-- =========================
     PAGE HEADER / HERO
========================= -->
<div class="page-hero">
    <div>
        <h1 class="page-title">Saved tips</h1>
        <p class="lead">Keep the tips you want to read again in one place.</p>
    </div>

    <!-- Navigation button -->
    <div class="hero-actions">
        <a href="${pageContext.request.contextPath}/user/tips" class="btn btn-secondary">
            Browse all tips
        </a>
    </div>
</div>

<!-- Flash messages (success/error alerts) -->
<jsp:include page="/includes/flash.jsp"/>

<!-- =========================
     WISHLIST ITEMS LIST
========================= -->
<c:forEach var="t" items="${tips}">
    <article class="content tip-card">

        <!-- Tip title -->
        <h2 style="margin-top:0;font-size:1.15rem">${t.title}</h2>

        <!-- Tip category -->
        <p class="small-muted">${t.category}</p>

        <!-- Tip content -->
        <p>${t.content}</p>

        <!-- Remove from wishlist -->
        <form method="post"
              action="${pageContext.request.contextPath}/user/wishlist"
              onsubmit="return confirm('Remove from wishlist?');">

            <!-- Hidden fields -->
            <input type="hidden" name="action" value="remove">
            <input type="hidden" name="tipId" value="${t.id}">

            <!-- Remove button -->
            <button type="submit" class="btn btn-danger btn-sm">
                Remove
            </button>
        </form>
    </article>
</c:forEach>

<!-- =========================
     EMPTY STATE
========================= -->
<c:if test="${empty tips}">
    <div class="soft-panel">
        <p class="small-muted">
            Your wishlist is empty.
            <!-- Link to browse tips -->
            <a href="${pageContext.request.contextPath}/user/tips">
                Browse tips
            </a>
        </p>
    </div>
</c:if>

<!-- Include footer -->
<jsp:include page="/includes/footer.jsp"/>