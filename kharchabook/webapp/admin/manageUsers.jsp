<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Manage Users" scope="request"/>
<jsp:include page="/includes/header.jsp"/>

<div class="page-hero">
    <div>
        <h1 class="page-title">Manage users</h1>
        <p class="lead">Review registered users, approve pending accounts and block accounts when needed.</p>
    </div>
</div>

<jsp:include page="/includes/flash.jsp"/>

<div class="layout-split">
    <div class="stack">
        <form method="get" action="${pageContext.request.contextPath}/admin/users" class="soft-panel">
            <div class="inline-form">
                <input type="text" name="search" value="${search}" placeholder="Search by name or phone">
                <button type="submit" class="btn btn-primary">Search</button>
                <c:if test="${not empty search}">
                    <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-secondary">Clear</a>
                </c:if>
            </div>
        </form>

        <div class="table-wrap">
            <table class="data">
                <thead>
                    <tr>
                        <th>Name</th>
                        <th>Email</th>
                        <th>Phone</th>
                        <th>Status</th>
                        <th>Joined</th>
                        <th></th>
                    </tr>
                </thead>
                <tbody>
                <c:forEach var="u" items="${users}">
                    <tr>
                        <td data-label="Name">
                            <a href="${pageContext.request.contextPath}/admin/users?id=${u.id}">${u.fullName}</a>
                        </td>
                        <td data-label="Email">${u.email}</td>
                        <td data-label="Phone">${u.phone}</td>
                        <td data-label="Status">${u.status}</td>
                        <td data-label="Joined">${u.createdAt}</td>
                        <td data-label="Actions">
                            <div class="btn-group">
                                <a href="${pageContext.request.contextPath}/admin/users?id=${u.id}" class="btn btn-secondary btn-sm">View</a>
                                <c:if test="${u.status == 'PENDING'}">
                                    <form method="post" action="${pageContext.request.contextPath}/admin/users">
                                        <input type="hidden" name="action" value="approve">
                                        <input type="hidden" name="id" value="${u.id}">
                                        <button type="submit" class="btn btn-primary btn-sm">Approve</button>
                                    </form>
                                </c:if>
                                <c:if test="${u.status == 'ACTIVE'}">
                                    <form method="post" action="${pageContext.request.contextPath}/admin/users" onsubmit="return confirm('Block this user?');">
                                        <input type="hidden" name="action" value="block">
                                        <input type="hidden" name="id" value="${u.id}">
                                        <button type="submit" class="btn btn-danger btn-sm">Block</button>
                                    </form>
                                </c:if>
                                <c:if test="${u.status == 'BLOCKED'}">
                                    <form method="post" action="${pageContext.request.contextPath}/admin/users">
                                        <input type="hidden" name="action" value="unblock">
                                        <input type="hidden" name="id" value="${u.id}">
                                        <button type="submit" class="btn btn-primary btn-sm">Unblock</button>
                                    </form>
                                </c:if>
                            </div>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty users}">
                    <tr>
                        <td colspan="6">No users found.</td>
                    </tr>
                </c:if>
                </tbody>
            </table>
        </div>
    </div>

    <div class="stack">
        <div class="soft-panel">
            <h2 class="panel-title">User workflow</h2>
            <ul class="summary-list">
                <li>Pending users cannot log in until approved.</li>
                <li>Active users can access their dashboard and tools.</li>
                <li>Blocked users are prevented from logging in.</li>
            </ul>
        </div>
    </div>
</div>

<jsp:include page="/includes/footer.jsp"/>
