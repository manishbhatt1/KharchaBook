<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Admin dashboard" scope="request"/>
<jsp:include page="/includes/header.jsp"/>

<div class="page-hero">
    <div>
        <h1 class="page-title">Admin dashboard</h1>
        <p class="lead">A quick overview of registrations, system activity and user behaviour.</p>
    </div>
    <div class="hero-actions">
        <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-primary">Manage users</a>
        <a href="${pageContext.request.contextPath}/admin/reports" class="btn btn-secondary">Open reports</a>
    </div>
</div>

<jsp:include page="/includes/flash.jsp"/>

<div class="grid">
    <div class="card">
        <h3>Registered users</h3>
        <div class="stat-value">${totalUsers}</div>
    </div>
    <div class="card">
        <h3>Total transactions</h3>
        <div class="stat-value">${totalTransactions}</div>
    </div>
    <div class="card">
        <h3>Most used expense category</h3>
        <div class="stat-value" style="font-size:1rem">${topCategoryName}</div>
    </div>
    <div class="card">
        <h3>New users this month</h3>
        <div class="stat-value">${regThisMonth}</div>
        <p class="small-muted">Previous month: ${regPrevMonth}</p>
    </div>
</div>

<div class="layout-split">
    <div class="stack">
        <div class="soft-panel">
            <h2 class="panel-title">Most active users</h2>
        </div>
        <div class="table-wrap">
            <table class="data">
                <thead><tr><th>Name</th><th>Transactions</th></tr></thead>
                <tbody>
                <c:forEach var="row" items="${topActiveUsers}">
                    <tr>
                        <td><a href="${pageContext.request.contextPath}/admin/users?id=${row[0]}">${row[1]}</a></td>
                        <td>${row[2]}</td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>

        <div class="soft-panel">
            <h2 class="panel-title">Recent registrations</h2>
        </div>
        <div class="table-wrap">
            <table class="data">
                <thead><tr><th>Name</th><th>Email</th><th>Status</th><th>Registered</th></tr></thead>
                <tbody>
                <c:forEach var="u" items="${recentUsers}">
                    <tr>
                        <td><a href="${pageContext.request.contextPath}/admin/users?id=${u.id}">${u.fullName}</a></td>
                        <td>${u.email}</td>
                        <td>${u.status}</td>
                        <td>${u.createdAt}</td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>

    <div class="stack">
        <div class="soft-panel">
            <h2 class="panel-title">Admin shortcuts</h2>
            <ul class="action-list">
                <li><a href="${pageContext.request.contextPath}/admin/users">Review users</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/categories">Edit categories</a></li>
                <li><a href="${pageContext.request.contextPath}/admin/tips">Manage financial tips</a></li>
            </ul>
        </div>
    </div>
</div>

<jsp:include page="/includes/footer.jsp"/>
