<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Financial tips" scope="request"/>
<jsp:include page="/includes/header.jsp"/>

<div class="page-hero">
    <div>
        <h1 class="page-title">Financial literacy tips</h1>
        <p class="lead">Search by topic and save useful tips to your wishlist.</p>
    </div>
</div>

<jsp:include page="/includes/flash.jsp"/>

<div class="layout-split">
    <div class="stack">
        <form method="get" action="${pageContext.request.contextPath}/user/tips" class="card">
            <div class="form-row">
                <label>Search</label>
                <input type="text" name="q" value="${searchQ}" placeholder="Keyword">
            </div>
            <div class="form-row">
                <label>Category</label>
                <select name="category">
                    <option value="all" ${empty searchCat || searchCat == 'all' ? 'selected' : ''}>All</option>
                    <option value="Saving" ${searchCat == 'Saving' ? 'selected' : ''}>Saving</option>
                    <option value="Budgeting" ${searchCat == 'Budgeting' ? 'selected' : ''}>Budgeting</option>
                    <option value="Debt" ${searchCat == 'Debt' ? 'selected' : ''}>Debt</option>
                    <option value="Investment" ${searchCat == 'Investment' ? 'selected' : ''}>Investment</option>
                    <option value="General" ${searchCat == 'General' ? 'selected' : ''}>General</option>
                </select>
            </div>
            <button type="submit" class="btn btn-secondary">Search</button>
        </form>

        <c:forEach var="t" items="${tips}">
            <article class="content tip-card">
                <h2 style="margin-top:0;font-size:1.15rem">${t.title}</h2>
                <p class="small-muted">${t.category} | ${t.postedDate}</p>
                <p>${t.content}</p>
                <c:choose>
                    <c:when test="${t.wishlistedByCurrentUser}">
                        <span class="role-pill">Saved</span>
                    </c:when>
                    <c:otherwise>
                        <form method="post" action="${pageContext.request.contextPath}/user/wishlist" style="display:inline">
                            <input type="hidden" name="action" value="add">
                            <input type="hidden" name="tipId" value="${t.id}">
                            <input type="hidden" name="from" value="tips">
                            <button type="submit" class="btn btn-primary btn-sm">Add to wishlist</button>
                        </form>
                    </c:otherwise>
                </c:choose>
            </article>
        </c:forEach>
        <c:if test="${empty tips}">
            <div class="soft-panel">
                <p class="small-muted">No tips found.</p>
            </div>
        </c:if>
    </div>

    <div class="stack">
        <div class="soft-panel">
            <h2 class="panel-title">Browse by purpose</h2>
            <ul class="summary-list">
                <li>Saving for future needs</li>
                <li>Building a monthly budget</li>
                <li>Reducing unnecessary debt</li>
                <li>Understanding basic investing</li>
            </ul>
        </div>
    </div>
</div>

<jsp:include page="/includes/footer.jsp"/>
