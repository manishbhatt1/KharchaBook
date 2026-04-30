<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Savings goals" scope="request"/>
<jsp:include page="/includes/header.jsp"/>

<div class="page-hero">
    <div>
        <h1 class="page-title">Savings goals</h1>
        <p class="lead">Create targets and keep adding money as you move toward them.</p>
    </div>
    <div class="hero-actions">
        <a href="${pageContext.request.contextPath}/user/goals?action=new" class="btn btn-primary">New goal</a>
    </div>
</div>

<jsp:include page="/includes/flash.jsp"/>

<div class="layout-split">
    <div class="stack">
        <div class="soft-panel">
            <h2 class="panel-title">Active goals</h2>
        </div>
        <c:forEach var="g" items="${activeGoals}">
            <div class="card">
                <strong>${g.title}</strong>
                <p class="small-muted">Target NPR ${g.targetAmount} | Saved NPR ${g.savedAmount}<c:if test="${not empty g.deadline}"> | Deadline ${g.deadline}</c:if></p>
                <div class="progress">
                    <div class="progress-bar" style="width:${g.percentComplete > 100 ? 100 : g.percentComplete}%"></div>
                </div>
                <form method="post" action="${pageContext.request.contextPath}/user/goals" class="inline-form section-space">
                    <input type="hidden" name="action" value="addMoney">
                    <input type="hidden" name="id" value="${g.id}">
                    <input type="number" name="amount" step="0.01" min="0.01" placeholder="Add amount">
                    <button type="submit" class="btn btn-primary btn-sm">Add money</button>
                </form>
                <div class="btn-group" style="margin-top:0.75rem">
                    <a href="${pageContext.request.contextPath}/user/goals?action=edit&id=${g.id}" class="btn btn-secondary btn-sm">Edit</a>
                    <form method="post" style="display:inline" action="${pageContext.request.contextPath}/user/goals">
                        <input type="hidden" name="action" value="complete">
                        <input type="hidden" name="id" value="${g.id}">
                        <button type="submit" class="btn btn-secondary btn-sm">Mark complete</button>
                    </form>
                    <form method="post" style="display:inline" action="${pageContext.request.contextPath}/user/goals" onsubmit="return confirm('Cancel this goal?');">
                        <input type="hidden" name="action" value="cancel">
                        <input type="hidden" name="id" value="${g.id}">
                        <button type="submit" class="btn btn-danger btn-sm">Cancel</button>
                    </form>
                </div>
            </div>
        </c:forEach>
        <c:if test="${empty activeGoals}">
            <div class="soft-panel">
                <p class="small-muted">No active goals.</p>
            </div>
        </c:if>

        <div class="soft-panel">
            <h2 class="panel-title">Completed goals</h2>
            <c:forEach var="g" items="${completedGoals}">
                <div class="metric-card" style="margin-top:0.6rem">
                    <strong>${g.title}</strong>
                    <span>NPR ${g.savedAmount} / ${g.targetAmount}</span>
                </div>
            </c:forEach>
            <c:if test="${empty completedGoals}">
                <p class="small-muted">No completed goals yet.</p>
            </c:if>
        </div>
    </div>

    <div class="stack">
        <div class="soft-panel">
            <h2 class="panel-title">Goal ideas</h2>
            <ul class="summary-list">
                <li>Emergency fund</li>
                <li>School or institution fees</li>
                <li>Laptop or phone purchase</li>
                <li>Education fees</li>
                <li>Festival spending reserve</li>
            </ul>
        </div>
    </div>
</div>

<jsp:include page="/includes/footer.jsp"/>
