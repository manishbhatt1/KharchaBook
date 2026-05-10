<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Budgets" scope="request"/>
<jsp:include page="/includes/header.jsp"/>

<div class="page-hero">
    <div>
        <h1 class="page-title">Monthly budgets</h1>
        <p class="lead">Set category limits for one month and check how much has already been spent.</p>
    </div>
</div>

<jsp:include page="/includes/flash.jsp"/>

<div class="layout-split">
    <div class="stack">
        <form method="get" action="${pageContext.request.contextPath}/user/budgets" class="card inline-form">
            <div class="form-row">
                <label>Budget month</label>
                <input type="month" name="budgetMonth" value="${budgetMonth}" required>
            </div>
            <button type="submit" class="btn btn-secondary">Go</button>
        </form>

        <div class="soft-panel">
            <h2 class="panel-title">Current budgets</h2>
            <p class="small-muted">Each bar shows how much of the limit is already used.</p>
        </div>

        <c:forEach var="b" items="${budgets}">
            <div class="card">
                <strong>${b.categoryName}</strong>
                <p class="small-muted">Limit NPR ${b.monthlyLimit} | Spent NPR ${b.spentAmount}</p>
                <div class="progress">
                    <c:set var="pct" value="${b.percentUsed}"/>
                    <div class="progress-bar ${pct >= 100 ? 'danger' : (pct >= 80 ? 'warn' : '')}" style="width:${pct > 100 ? 100 : pct}%"></div>
                </div>
                <div class="btn-group" style="margin-top:0.75rem">
                    <a href="${pageContext.request.contextPath}/user/budgets?action=edit&id=${b.id}&budgetMonth=${budgetMonth}" class="btn btn-secondary btn-sm">Edit limit</a>
                    <form method="post" action="${pageContext.request.contextPath}/user/budgets" style="display:inline" onsubmit="return confirm('Remove this budget?');">
                        <input type="hidden" name="action" value="delete">
                        <input type="hidden" name="id" value="${b.id}">
                        <input type="hidden" name="budgetMonth" value="${budgetMonth}">
                        <button type="submit" class="btn btn-danger btn-sm">Delete</button>
                    </form>
                </div>
            </div>
        </c:forEach>

        <c:if test="${empty budgets}">
            <div class="soft-panel">
                <p class="small-muted">No budgets for this month yet.</p>
            </div>
        </c:if>

        <c:if test="${not empty editBudget}">
            <form method="post" action="${pageContext.request.contextPath}/user/budgets" class="card">
                <h2 class="panel-title">Edit budget</h2>
                <input type="hidden" name="action" value="update">
                <input type="hidden" name="id" value="${editBudget.id}">
                <input type="hidden" name="budgetMonth" value="${budgetMonth}">
                <p><strong>${editBudget.categoryName}</strong></p>
                <div class="form-row">
                    <label>Monthly limit (NPR)</label>
                    <input type="number" name="monthlyLimit" step="0.01" min="0.01" value="${editBudget.monthlyLimit}" required>
                </div>
                <button type="submit" class="btn btn-primary">Save</button>
            </form>
        </c:if>

        <form method="post" action="${pageContext.request.contextPath}/user/budgets" class="card">
            <h2 class="panel-title">Set new budget</h2>
            <input type="hidden" name="action" value="create">
            <input type="hidden" name="budgetMonth" value="${budgetMonth}">
            <div class="form-row">
                <label>Expense category</label>
                <select name="categoryId" required>
                    <c:forEach var="c" items="${expenseCategories}">
                        <option value="${c.id}">${c.name}</option>
                    </c:forEach>
                </select>
            </div>
            <div class="form-row">
                <label>Monthly limit (NPR)</label>
                <input type="number" name="monthlyLimit" step="0.01" min="0.01" required>
            </div>
            <button type="submit" class="btn btn-primary">Add budget</button>
        </form>
    </div>

    <div class="stack">
        <div class="soft-panel">
            <h2 class="panel-title">Budget tips</h2>
            <ul class="summary-list">
                <li>Set higher limits for categories you use every week.</li>
                <li>Review food, transport and rent first.</li>
                <li>If a budget often exceeds, adjust the limit or cut spending.</li>
            </ul>
        </div>
    </div>
</div>

<jsp:include page="/includes/footer.jsp"/>
