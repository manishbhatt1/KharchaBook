<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Edit goal" scope="request"/>
<jsp:include page="/includes/header.jsp"/>
<div class="page-hero">
    <div>
        <h1 class="page-title">Edit savings goal</h1>
        <p class="lead">Update the target or deadline if the plan changes.</p>
    </div>
</div>
<form method="post" action="${pageContext.request.contextPath}/user/goals" class="card">
    <input type="hidden" name="action" value="update">
    <input type="hidden" name="id" value="${goal.id}">
    <div class="form-row">
        <label>Title</label>
        <input type="text" name="title" value="${goal.title}" required maxlength="200">
    </div>
    <div class="form-row">
        <label>Target amount (NPR)</label>
        <input type="number" name="targetAmount" step="0.01" min="0.01" value="${goal.targetAmount}" required>
    </div>
    <div class="form-row">
        <label>Deadline</label>
        <input type="date" name="deadline" value="${goal.deadline}">
    </div>
    <button type="submit" class="btn btn-primary">Save</button>
    <a href="${pageContext.request.contextPath}/user/goals" class="btn btn-secondary">Back</a>
</form>
<jsp:include page="/includes/footer.jsp"/>
