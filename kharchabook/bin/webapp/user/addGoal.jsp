<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="New savings goal" scope="request"/>
<jsp:include page="/includes/header.jsp"/>
<div class="page-hero">
    <div>
        <h1 class="page-title">New savings goal</h1>
        <p class="lead">You can use a goal to set aside money for school fees, rent, emergencies, or any planned cost.</p>
    </div>
</div>
<div class="layout-split">
    <form method="post" action="${pageContext.request.contextPath}/user/goals" class="card">
        <input type="hidden" name="action" value="create">
        <div class="form-row">
            <label>Title</label>
            <input type="text" name="title" required maxlength="200">
        </div>
        <div class="form-row">
            <label>Target amount (NPR)</label>
            <input type="number" name="targetAmount" step="0.01" min="0.01" required>
        </div>
        <div class="form-row">
            <label>Deadline (optional)</label>
            <input type="date" name="deadline">
        </div>
        <button type="submit" class="btn btn-primary">Create</button>
        <a href="${pageContext.request.contextPath}/user/goals" class="btn btn-secondary">Back</a>
    </form>
    <div class="soft-panel">
        <h2 class="panel-title">Examples</h2>
        <ul class="summary-list">
            <li>School fees for next term</li>
            <li>Rent for next month</li>
            <li>Emergency medical fund</li>
            <li>Festival spending reserve</li>
        </ul>
    </div>
</div>
<jsp:include page="/includes/footer.jsp"/>
