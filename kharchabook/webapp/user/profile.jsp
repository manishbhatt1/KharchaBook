<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Profile" scope="request"/>
<jsp:include page="/includes/header.jsp"/>
<h1 class="page-title">Profile</h1>
<jsp:include page="/includes/flash.jsp"/>
<form method="post" action="${pageContext.request.contextPath}/user/profile">
    <div class="form-row">
        <label>Full name</label>
        <input type="text" name="fullName" value="${profileUser.fullName}" required>
    </div>
    <div class="form-row">
        <label>Phone</label>
        <input type="tel" name="phone" value="${profileUser.phone}" required>
    </div>
    <div class="form-row">
        <label>Email</label>
        <input type="email" name="email" value="${profileUser.email}" required>
    </div>
    <h2 style="font-size:1.05rem">Change password (optional)</h2>
    <div class="form-row">
        <label>Current password</label>
        <input type="password" name="currentPassword" autocomplete="current-password">
    </div>
    <div class="form-row">
        <label>New password</label>
        <input type="password" name="newPassword" autocomplete="new-password">
    </div>
    <button type="submit" class="btn btn-primary">Save changes</button>
</form>
<jsp:include page="/includes/footer.jsp"/>
