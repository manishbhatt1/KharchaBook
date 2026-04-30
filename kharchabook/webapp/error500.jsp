<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" isErrorPage="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Server error" scope="request"/>
<jsp:include page="/includes/header.jsp"/>
<h1 class="page-title">500 - Something went wrong</h1>
<p>We are sorry. Please try again later or contact support.</p>
<c:if test="${not empty exception}">
    <div style="background:#fff6f6; border:1px solid #f5c2c7; padding:1rem; margin:1rem 0; border-radius:6px; color:#6a1b1f;">
        <h3>Error details</h3>
        <p><strong>${exception.message}</strong></p>
        <pre style="white-space:pre-wrap; font-size:0.9rem; overflow-x:auto;">${exception.stackTrace}</pre>
    </div>
</c:if>
<p><a href="${pageContext.request.contextPath}/index.jsp">Go home</a></p>
<jsp:include page="/includes/footer.jsp"/>
