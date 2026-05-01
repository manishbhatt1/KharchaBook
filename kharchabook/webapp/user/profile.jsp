<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- Set page title -->
<c:set var="pageTitle" value="Profile" scope="request"/>

<!-- Include header -->
<jsp:include page="/includes/header.jsp"/>

<!-- =========================
     PAGE TITLE
========================= -->
<h1 class="page-title">Profile</h1>

<!-- Flash messages (success/error) -->
<jsp:include page="/includes/flash.jsp"/>

<!-- =========================
     PROFILE UPDATE FORM
========================= -->
<form method="post" action="${pageContext.request.contextPath}/user/profile">

    <!-- Full Name -->
    <div class="form-row">
        <label>Full name</label>
        <input type="text" name="fullName" value="${profileUser.fullName}" required>
    </div>

    <!-- Phone Number -->
    <div class="form-row">
        <label>Phone</label>
        <input type="tel" name="phone" value="${profileUser.phone}" required>
    </div>

    <!-- Email Address -->
    <div class="form-row">
        <label>Email</label>
        <input type="email" name="email" value="${profileUser.email}" required>
    </div>

    <!-- =========================
         PASSWORD CHANGE SECTION
    ========================= -->
    <h2 style="font-size:1.05rem">Change password (optional)</h2>

    <!-- Current Password -->
    <div class="form-row">
        <label>Current password</label>
        <input type="password" name="currentPassword" autocomplete="current-password">
    </div>

    <!-- New Password -->
    <div class="form-row">
        <label>New password</label>
        <input type="password" name="newPassword" autocomplete="new-password">
    </div>

    <!-- Submit button -->
    <button type="submit" class="btn btn-primary">Save changes</button>
</form>

<!-- Include footer -->
<jsp:include page="/includes/footer.jsp"/>