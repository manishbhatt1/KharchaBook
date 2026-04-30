<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Page not found" scope="request"/>
<jsp:include page="/includes/header.jsp"/>
<h1 class="page-title">404 - Page not found</h1>
<p>The page you requested does not exist.</p>
<p><a href="${pageContext.request.contextPath}/index.jsp">Go home</a></p>
<jsp:include page="/includes/footer.jsp"/>
