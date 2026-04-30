<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="User detail" scope="request"/>
<jsp:include page="/includes/header.jsp"/>

<c:choose>
    <c:when test="${not empty detailUser}">
        <div class="page-hero">
            <div>
                <h1 class="page-title">User account</h1>
                <p class="lead">Account details and current activity summary.</p>
            </div>
            <div class="hero-actions">
                <a href="${pageContext.request.contextPath}/admin/users" class="btn btn-secondary">Back to users</a>
            </div>
        </div>

        <div class="layout-split">
            <div class="stack">
                <div class="card">
                    <p><strong>Name:</strong> ${detailUser.fullName}</p>
                    <p><strong>Phone:</strong> ${detailUser.phone}</p>
                    <p><strong>Email:</strong> ${detailUser.email}</p>
                    <p><strong>Status:</strong> ${detailUser.status}</p>
                    <p><strong>Registered:</strong> ${detailUser.createdAt}</p>
                    <p><strong>Total transactions:</strong> ${txCount}</p>
                </div>
            </div>

            <div class="stack">
                <div class="soft-panel">
                    <h2 class="panel-title">Admin actions</h2>
                    <ul class="summary-list">
                        <li>Review the account status before changing access.</li>
                        <li>Open the users page to approve, block or unblock.</li>
                        <li>Use transaction count to estimate activity level.</li>
                    </ul>
                </div>
            </div>
        </div>
    </c:when>
    <c:otherwise>
        <div class="soft-panel">
            <h1 class="page-title">User account</h1>
            <p class="small-muted">User not found.</p>
            <p><a href="${pageContext.request.contextPath}/admin/users" class="btn btn-secondary">Back</a></p>
        </div>
    </c:otherwise>
</c:choose>

<jsp:include page="/includes/footer.jsp"/>
