<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- Set page title -->
<c:set var="pageTitle" value="Financial tips" scope="request"/>

<!-- Include common header -->
<jsp:include page="/includes/header.jsp"/>

<!-- =========================
     PAGE HEADER
========================= -->
<div class="page-hero">
    <div>
        <h1 class="page-title">Financial literacy tips</h1>
        <p class="lead">Search by topic and save useful tips to your wishlist.</p>
    </div>
</div>

<!-- Flash messages (success / error alerts) -->
<jsp:include page="/includes/flash.jsp"/>

<div class="layout-split">

    <!-- =========================
         LEFT SIDE (SEARCH + TIPS)
    ========================= -->
    <div class="stack">

        <!-- Search and filter form -->
        <form method="get" action="${pageContext.request.contextPath}/user/tips" class="card">
            <div class="form-row">
                <label>Search</label>
                <!-- Search input -->
                <input type="text" name="q" value="${searchQ}" placeholder="Keyword">
            </div>

            <div class="form-row">
                <label>Category</label>
                <!-- Category dropdown filter -->
                <select name="category">
                    <option value="all" ${empty searchCat || searchCat == 'all' ? 'selected' : ''}>All</option>
                    <option value="Saving" ${searchCat == 'Saving' ? 'selected' : ''}>Saving</option>
                    <option value="Budgeting" ${searchCat == 'Budgeting' ? 'selected' : ''}>Budgeting</option>
                    <option value="Debt" ${searchCat == 'Debt' ? 'selected' : ''}>Debt</option>
                    <option value="Investment" ${searchCat == 'Investment' ? 'selected' : ''}>Investment</option>
                    <option value="General" ${searchCat == 'General' ? 'selected' : ''}>General</option>
                </select>
            </div>

            <!-- Submit search -->
            <button type="submit" class="btn btn-secondary">Search</button>
        </form>

        <!-- =========================
             TIPS LIST
        ========================= -->
        <c:forEach var="t" items="${tips}">
            <article class="content tip-card">

                <!-- Tip title -->
                <h2 style="margin-top:0;font-size:1.15rem">${t.title}</h2>

                <!-- Metadata -->
                <p class="small-muted">${t.category} | ${t.postedDate}</p>

                <!-- Tip content -->
                <p>${t.content}</p>

                <!-- Wishlist logic -->
                <c:choose>

                    <!-- If already wishlisted -->
                    <c:when test="${t.wishlistedByCurrentUser}">
                        <span class="role-pill">Saved</span>
                    </c:when>

                    <!-- If not wishlisted -->
                    <c:otherwise>
                        <form method="post" action="${pageContext.request.contextPath}/user/wishlist" style="display:inline">
                            
                            <!-- Hidden fields -->
                            <input type="hidden" name="action" value="add">
                            <input type="hidden" name="tipId" value="${t.id}">
                            <input type="hidden" name="from" value="tips">

                            <!-- Add button -->
                            <button type="submit" class="btn btn-primary btn-sm">
                                Add to wishlist
                            </button>
                        </form>
                    </c:otherwise>

                </c:choose>
            </article>
        </c:forEach>

        <!-- If no tips found -->
        <c:if test="${empty tips}">
            <div class="soft-panel">
                <p class="small-muted">No tips found.</p>
            </div>
        </c:if>
    </div>

    <!-- =========================
         RIGHT SIDE (INFO PANEL)
    ========================= -->
    <div class="stack">
        <div class="soft-panel">
            <h2 class="panel-title">Browse by purpose</h2>

            <!-- Static guidance list -->
            <ul class="summary-list">
                <li>Saving for future needs</li>
                <li>Building a monthly budget</li>
                <li>Reducing unnecessary debt</li>
                <li>Understanding basic investing</li>
            </ul>
        </div>
    </div>
</div>

<!-- Include footer -->
<jsp:include page="/includes/footer.jsp"/>