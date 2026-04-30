<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Manage users" scope="request"/>
<jsp:include page="/includes/header.jsp"/>

<div class="page-hero">
    <div>
        <h1 class="page-title">Users</h1>
        <p class="lead">Search users, review status and approve or block accounts.</p>
    </div>
</div>

<jsp:include page="/includes/flash.jsp"/>

<div class="layout-split">
    <div class="stack">
        <form method="get" action="${pageContext.request.contextPath}/admin/users" class="card inline-form">
            <div class="form-row">
                <label>Search name or phone</label>
                <input type="text" name="search" value="${search}">
            </div>
            <button type="submit" class="btn btn-secondary">Search</button>
        </form>

        <div class="table-wrap">
            <table class="data">
                <thead>
                <tr><th>Name</th><th>Phone</th><th>Email</th><th>Status</th><th>Registered</th><th></th></tr>
                </thead>
                <tbody>
                <c:forEach var="u" items="${users}">
                    <tr>
                        <td><a href="${pageContext.request.contextPath}/admin/users?id=${u.id}">${u.fullName}</a></td>
                        <td>${u.phone}</td>
                        <td>${u.email}</td>
                        <td>${u.status}</td>
                        <td>${u.createdAt}</td>
                        <td>
                            <c:if test="${u.status == 'PENDING'}">
                                <form method="post" action="${pageContext.request.contextPath}/admin/users" style="display:inline">
                                    <input type="hidden" name="action" value="approve">
                                    <input type="hidden" name="id" value="${u.id}">
                                    <button type="submit" class="btn btn-primary btn-sm">Approve</button>
                                </form>
                            </c:if>
                            <c:if test="${u.status == 'APPROVED'}">
                                <form method="post" action="${pageContext.request.contextPath}/admin/users" style="display:inline" onsubmit="return confirm('Block this user?');">
                                    <input type="hidden" name="action" value="block">
                                    <input type="hidden" name="id" value="${u.id}">
                                    <button type="submit" class="btn btn-danger btn-sm">Block</button>
                                </form>
                            </c:if>
                            <c:if test="${u.status == 'BLOCKED'}">
                                <form method="post" action="${pageContext.request.contextPath}/admin/users" style="display:inline">
                                    <input type="hidden" name="action" value="unblock">
                                    <input type="hidden" name="id" value="${u.id}">
                                    <button type="submit" class="btn btn-secondary btn-sm">Unblock</button>
                                </form>
                            </c:if>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>

    <div class="stack">
        <div class="soft-panel">
            <h2 class="panel-title">Status guide</h2>
            <ul class="summary-list">
                <li>`PENDING` users are waiting for approval.</li>
                <li>`APPROVED` users can use the system normally.</li>
                <li>`BLOCKED` users cannot sign in until unblocked.</li>
            </ul>
        </div>
    </div>
</div>

<jsp:include page="/includes/footer.jsp"/>
