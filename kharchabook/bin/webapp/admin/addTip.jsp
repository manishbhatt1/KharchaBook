<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Add tip" scope="request"/>
<jsp:include page="/includes/header.jsp"/>
<h1 class="page-title">Publish tip</h1>
<jsp:include page="/includes/flash.jsp"/>
<form method="post" action="${pageContext.request.contextPath}/admin/tips">
    <input type="hidden" name="action" value="create">
    <div class="form-row">
        <label>Title</label>
        <input type="text" name="title" required maxlength="200">
    </div>
    <div class="form-row">
        <label>Category</label>
        <select name="category">
            <option value="Saving">Saving</option>
            <option value="Budgeting">Budgeting</option>
            <option value="Debt">Debt</option>
            <option value="Investment">Investment</option>
            <option value="General">General</option>
        </select>
    </div>
    <div class="form-row">
        <label>Content</label>
        <textarea name="content" required></textarea>
    </div>
    <button type="submit" class="btn btn-primary">Publish</button>
    <a href="${pageContext.request.contextPath}/admin/tips" class="btn btn-secondary">Cancel</a>
</form>
<jsp:include page="/includes/footer.jsp"/>
