<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Transactions" scope="request"/>
<jsp:include page="/includes/header.jsp"/>

<div class="page-hero">
    <div>
        <h1 class="page-title">Transactions</h1>
        <p class="lead">Filter your records by year, month, type and category.</p>
    </div>
    <div class="hero-actions">
        <a href="${pageContext.request.contextPath}/user/transactions?action=add" class="btn btn-primary">Add transaction</a>
    </div>
</div>

<jsp:include page="/includes/flash.jsp"/>

<div class="layout-split">
    <div class="stack">
        <form method="get" action="${pageContext.request.contextPath}/user/transactions" class="card">
            <div class="grid">
                <div class="form-row">
                    <label>Year</label>
                    <input type="number" name="year" value="${filterYear}" min="2000" max="2100">
                </div>
                <div class="form-row">
                    <label>Month</label>
                    <select name="month">
                        <option value="">All months</option>
                        <c:forEach var="m" begin="1" end="12">
                            <option value="${m}" ${filterMonth != null && filterMonth == m ? 'selected' : ''}>${m}</option>
                        </c:forEach>
                    </select>
                </div>
                <div class="form-row">
                    <label>Type</label>
                    <select name="type">
                        <option value="all" ${empty filterType || filterType == 'all' ? 'selected' : ''}>All</option>
                        <option value="income" ${filterType == 'income' ? 'selected' : ''}>Income</option>
                        <option value="expense" ${filterType == 'expense' ? 'selected' : ''}>Expense</option>
                    </select>
                </div>
                <div class="form-row">
                    <label>Category</label>
                    <select name="categoryId">
                        <option value="0">All</option>
                        <optgroup label="Income">
                            <c:forEach var="c" items="${incomeCategories}">
                                <option value="${c.id}" ${filterCategoryId != null && filterCategoryId == c.id ? 'selected' : ''}>${c.name}</option>
                            </c:forEach>
                        </optgroup>
                        <optgroup label="Expense">
                            <c:forEach var="c" items="${expenseCategories}">
                                <option value="${c.id}" ${filterCategoryId != null && filterCategoryId == c.id ? 'selected' : ''}>${c.name}</option>
                            </c:forEach>
                        </optgroup>
                    </select>
                </div>
            </div>
            <button type="submit" class="btn btn-secondary btn-sm">Apply filters</button>
        </form>

        <div class="table-wrap">
            <table class="data">
                <thead>
                <tr><th>Date</th><th>Type</th><th>Category</th><th>Amount</th><th>Note</th><th></th></tr>
                </thead>
                <tbody>
                <c:forEach var="t" items="${transactions}">
                    <tr>
                        <td>${t.transactionDate}</td>
                        <td>${t.type}</td>
                        <td>${t.categoryName}</td>
                        <td>NPR ${t.amount}</td>
                        <td>${t.description}</td>
                        <td>
                            <a class="btn btn-secondary btn-sm" href="${pageContext.request.contextPath}/user/transactions?action=edit&id=${t.id}">Edit</a>
                            <form method="post" action="${pageContext.request.contextPath}/user/transactions" style="display:inline" onsubmit="return confirm('Delete this transaction?');">
                                <input type="hidden" name="action" value="delete">
                                <input type="hidden" name="id" value="${t.id}">
                                <button type="submit" class="btn btn-danger btn-sm">Delete</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                <c:if test="${empty transactions}">
                    <tr><td colspan="6">No transactions match your filters.</td></tr>
                </c:if>
                </tbody>
            </table>
        </div>
    </div>

    <div class="stack">
        <div class="soft-panel">
            <h2 class="panel-title">Filter guide</h2>
            <ul class="summary-list">
                <li>Choose a year to see one full period.</li>
                <li>Use type to separate income from expense.</li>
                <li>Use category to focus on one spending area.</li>
            </ul>
        </div>
        <div class="soft-panel">
            <h2 class="panel-title">Useful actions</h2>
            <ul class="action-list">
                <li><a href="${pageContext.request.contextPath}/user/transactions?action=add">Add a new transaction</a></li>
                <li><a href="${pageContext.request.contextPath}/user/reports">Open reports</a></li>
                <li><a href="${pageContext.request.contextPath}/user/budgets">Review budgets</a></li>
            </ul>
        </div>
    </div>
</div>

<jsp:include page="/includes/footer.jsp"/>
