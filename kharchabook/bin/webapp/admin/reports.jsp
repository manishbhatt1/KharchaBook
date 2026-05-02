<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Admin reports" scope="request"/>
<jsp:include page="/includes/header.jsp"/>

<div class="page-hero">
    <div>
        <h1 class="page-title">System analytics</h1>
        <p class="lead">Track registrations, total activity, popular categories and most saved tips.</p>
    </div>
</div>

<div class="grid">
    <div class="card">
        <h3>New users this month</h3>
        <div class="stat-value">${regThisMonth}</div>
        <p class="small-muted">Previous month: ${regPrevMonth}</p>
    </div>
    <div class="card">
        <h3>Total transactions</h3>
        <div class="stat-value">${totalTransactions}</div>
    </div>
    <div class="card">
        <h3>Most used expense category</h3>
        <div class="stat-value" style="font-size:1rem">
            <c:choose>
                <c:when test="${not empty mostUsedCategory}">${mostUsedCategory.name}</c:when>
                <c:otherwise>-</c:otherwise>
            </c:choose>
        </div>
    </div>
</div>

<div class="layout-split">
    <div class="stack">
        <div class="table-wrap">
            <table class="data">
                <thead><tr><th>Month</th><th>New users</th></tr></thead>
                <tbody>
                <c:forEach var="cnt" items="${registrationTrend}" varStatus="st">
                    <tr>
                        <td>${st.index + 1}</td>
                        <td>${cnt}</td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>

        <div class="table-wrap">
            <table class="data">
                <thead><tr><th>Name</th><th>Transactions</th></tr></thead>
                <tbody>
                <c:forEach var="row" items="${topActiveUsers}">
                    <tr><td>${row[1]}</td><td>${row[2]}</td></tr>
                </c:forEach>
                </tbody>
            </table>
        </div>

        <div class="table-wrap">
            <table class="data">
                <thead><tr><th>Title</th><th>Saves</th></tr></thead>
                <tbody>
                <c:forEach var="row" items="${mostWishlistedTips}">
                    <tr><td>${row[1]}</td><td>${row[2]}</td></tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>

    <div class="stack">
        <div class="soft-panel">
            <h2 class="panel-title">Report summary</h2>
            <ul class="summary-list">
                <li>Trend year: ${trendYear}</li>
                <li>Shows 12 months of registrations.</li>
                <li>Includes most active users and most saved tips.</li>
            </ul>
        </div>
    </div>
</div>

<jsp:include page="/includes/footer.jsp"/>
