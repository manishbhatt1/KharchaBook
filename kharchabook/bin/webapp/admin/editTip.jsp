<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Edit tip" scope="request"/>
<jsp:include page="/includes/header.jsp"/>
<h1 class="page-title">Edit tip</h1>
<c:if test="${not empty tip}">
<form method="post" action="${pageContext.request.contextPath}/admin/tips">
    <input type="hidden" name="action" value="update">
    <input type="hidden" name="id" value="${tip.id}">
    <div class="form-row">
        <label>Title</label>
        <input type="text" name="title" value="${tip.title}" required maxlength="200">
    </div>
    <div class="form-row">
        <label>Category</label>
        <select name="category">
            <option value="Saving" ${tip.category == 'Saving' ? 'selected' : ''}>Saving</option>
            <option value="Budgeting" ${tip.category == 'Budgeting' ? 'selected' : ''}>Budgeting</option>
            <option value="Debt" ${tip.category == 'Debt' ? 'selected' : ''}>Debt</option>
            <option value="Investment" ${tip.category == 'Investment' ? 'selected' : ''}>Investment</option>
            <option value="General" ${tip.category == 'General' ? 'selected' : ''}>General</option>
        </select>
    </div>
    <div class="form-row">
        <label>Content</label>
        <textarea name="content" required>${tip.content}</textarea>
    </div>
    <button type="submit" class="btn btn-primary">Save</button>
    <a href="${pageContext.request.contextPath}/admin/tips" class="btn btn-secondary">Back</a>
</form>
</c:if>
<jsp:include page="/includes/footer.jsp"/>
