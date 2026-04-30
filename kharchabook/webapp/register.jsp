<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Register" scope="request"/>
<jsp:include page="/includes/header.jsp"/>
<div class="auth-shell">
    <div class="auth-wrapper">
        <div class="auth-visual">
            <img src="${pageContext.request.contextPath}/images/finance-flatlay.jpg"
                 alt="Calculator, notebook and money on a desk"
                 class="auth-photo">
            <div class="auth-copy">
                <h2>Create a KharchaBook account</h2>
                <p>Start recording your income, spending and saving goals. Your account will be reviewed before it becomes active.</p>
            </div>
        </div>
        <div class="auth-form-container">
            <h1>Register</h1>
            <p class="lead">Your account will become active after admin approval.</p>
            <jsp:include page="/includes/flash.jsp"/>
            <form method="post" action="${pageContext.request.contextPath}/register">
                <div class="form-row">
                    <label for="fullName">Full name</label>
                    <input type="text" id="fullName" name="fullName" required>
                </div>
                <div class="form-row">
                    <label for="phone">Phone</label>
                    <input type="tel" id="phone" name="phone" required>
                </div>
                <div class="form-row">
                    <label for="email">Email</label>
                    <input type="email" id="email" name="email" required>
                </div>
                <div class="form-row">
                    <label for="password">Password</label>
                    <input type="password" id="password" name="password" required minlength="6">
                </div>
                <button type="submit" class="btn btn-primary btn-full" style="margin-top: 0.5rem">Submit registration</button>
            </form>
            <p style="margin-top:2rem; font-size: 0.95rem; color: var(--muted)">Already have an account? <a href="${pageContext.request.contextPath}/login.jsp">Sign in directly</a></p>
        </div>
    </div>
</div>
<jsp:include page="/includes/footer.jsp"/>
