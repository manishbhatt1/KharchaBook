<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>${empty pageTitle ? 'KharchaBook' : pageTitle} - KharchaBook</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<header class="site-header">
    <div class="inner">
        <a class="brand" href="${pageContext.request.contextPath}/index.jsp">KharchaBook
            <span>Your expenses, your control</span>
        </a>
        <nav>
            <ul>
                <c:choose>
                    <c:when test="${sessionScope.userRole == 'ADMIN'}">
                        <li><a href="${pageContext.request.contextPath}/admin/dashboard">Dashboard</a></li>
                        <li><a href="${pageContext.request.contextPath}/admin/users">Users</a></li>
                        <li><a href="${pageContext.request.contextPath}/admin/categories">Categories</a></li>
                        <li><a href="${pageContext.request.contextPath}/admin/tips">Tips</a></li>
                        <li><a href="${pageContext.request.contextPath}/admin/reports">Reports</a></li>
                        <li><a href="${pageContext.request.contextPath}/admin/profile">Profile</a></li>
                        <li><span class="role-pill">Admin</span></li>
                        <li><a href="${pageContext.request.contextPath}/logout">Logout</a></li>
                    </c:when>
                    <c:when test="${sessionScope.userRole == 'USER'}">
                        <li><a href="${pageContext.request.contextPath}/user/dashboard">Dashboard</a></li>
                        <li><a href="${pageContext.request.contextPath}/user/transactions">Transactions</a></li>
                        <li><a href="${pageContext.request.contextPath}/user/budgets">Budgets</a></li>
                        <li><a href="${pageContext.request.contextPath}/user/bills">Bills</a></li>
                        <li><a href="${pageContext.request.contextPath}/user/goals">Savings</a></li>
                        <li><a href="${pageContext.request.contextPath}/user/reports">Reports</a></li>
                        <li><a href="${pageContext.request.contextPath}/user/tips">Tips</a></li>
                        <li><a href="${pageContext.request.contextPath}/user/wishlist">Wishlist</a></li>
                        <li><a href="${pageContext.request.contextPath}/user/profile">Profile</a></li>
                        <li><span class="role-pill">User</span></li>
                        <li><a href="${pageContext.request.contextPath}/logout">Logout</a></li>
                    </c:when>
                    <c:otherwise>
                        <li><a href="${pageContext.request.contextPath}/login.jsp">Login</a></li>
                        <li><a href="${pageContext.request.contextPath}/register.jsp">Register</a></li>
                        <li><a href="${pageContext.request.contextPath}/about.jsp">About</a></li>
                        <li><a href="${pageContext.request.contextPath}/contact.jsp">Contact</a></li>
                    </c:otherwise>
                </c:choose>
            </ul>
        </nav>
    </div>
</header>
<main class="wrap">
