<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Login" scope="request"/>
<jsp:include page="/includes/header.jsp"/>

<div class="auth-shell">
    <div class="auth-wrapper login-wrapper">
        <div class="auth-visual login-visual">
            <img src="${pageContext.request.contextPath}/images/finance-flatlay.jpg"
                 alt="Calculator, notebook and Nepali rupees on a desk"
                 class="auth-photo">
            <div class="auth-copy">
                <h2>Track your daily money simply.</h2>
                <p>KharchaBook helps you record income, expenses, budgets and savings goals in one place.</p>
            </div>
        </div>

        <div class="auth-form-container">
            <div class="login-form-inner">
                <div class="login-greeting">
                    <h1>Login</h1>
                    <p class="lead">Use your email address or phone number.</p>
                </div>

                <jsp:include page="/includes/flash.jsp"/>

                <form method="post" action="${pageContext.request.contextPath}/login" id="loginForm">
                    <div class="form-row">
                        <label for="login">Email or phone number</label>
                        <input type="text" id="login" name="login" required
                               autocomplete="off" placeholder="you@example.com or 98XXXXXXXX"/>
                    </div>
                    <div class="form-row">
                        <label for="password">Password</label>
                        <input type="password" id="password" name="password" required
                               autocomplete="off" placeholder="Your password"/>
                    </div>

                    <div class="login-options">
                        <label class="remember-me">
                            <input type="checkbox" name="remember" id="remember"/>
                            <span>Remember me</span>
                        </label>
                        <a href="#" class="forgot-link">Forgot password?</a>
                    </div>

                    <button type="submit" class="btn btn-primary btn-full" id="loginBtn">
                        Sign in
                    </button>
                </form>

                <div class="login-divider"><span>or</span></div>

                <div class="login-cta">
                    <p>Don't have an account?</p>
                    <a href="${pageContext.request.contextPath}/register.jsp" class="btn btn-secondary btn-full">
                        Create an account
                    </a>
                </div>
            </div>
        </div>
    </div>
</div>

<jsp:include page="/includes/footer.jsp"/>
