<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Categories" scope="request"/>
<jsp:include page="/includes/header.jsp"/>

<div class="page-hero">
    <div>
        <h1 class="page-title">Categories</h1>
        <p class="lead">Maintain the income and expense categories used across the system.</p>
    </div>
</div>

<jsp:include page="/includes/flash.jsp"/>

<div class="layout-split">
    <div class="stack">
        <form method="post" action="${pageContext.request.contextPath}/admin/categories" class="card">
            <h2 class="panel-title">Add category</h2>
            <input type="hidden" name="action" value="create">
            <div class="form-row">
                <label>Name</label>
                <input type="text" name="name" required maxlength="100">
            </div>
            <div class="form-row">
                <label>Type</label>
                <select name="type">
                    <option value="income">Income</option>
                    <option value="expense">Expense</option>
                </select>
            </div>
            <div class="form-row">
                <label>Icon key (optional)</label>
                <input type="text" name="icon" maxlength="50">
            </div>
            <button type="submit" class="btn btn-primary">Add</button>
        </form>

        <c:if test="${not empty editCategory}">
            <form method="post" action="${pageContext.request.contextPath}/admin/categories" class="card">
                <h2 class="panel-title">Edit category</h2>
                <input type="hidden" name="action" value="update">
                <input type="hidden" name="id" value="${editCategory.id}">
                <div class="form-row">
                    <label>Name</label>
                    <input type="text" name="name" value="${editCategory.name}" required>
                </div>
                <div class="form-row">
                    <label>Type</label>
                    <select name="type">
                        <option value="income" ${editCategory.type == 'income' ? 'selected' : ''}>Income</option>
                        <option value="expense" ${editCategory.type == 'expense' ? 'selected' : ''}>Expense</option>
                    </select>
                </div>
                <button type="submit" class="btn btn-primary">Save</button>
            </form>
        </c:if>

        <div class="table-wrap">
            <table class="data">
                <thead><tr><th>Name</th><th>Type</th><th>Transactions</th><th></th></tr></thead>
                <tbody>
                <c:forEach var="c" items="${categories}">
                    <tr>
                        <td>${c.name}</td>
                        <td>${c.type}</td>
                        <td>${c.transactionCount}</td>
                        <td>
                            <a href="${pageContext.request.contextPath}/admin/categories?action=edit&id=${c.id}" class="btn btn-secondary btn-sm">Edit</a>
                            <form method="post" style="display:inline" action="${pageContext.request.contextPath}/admin/categories" onsubmit="return confirm('Delete only if unused.');">
                                <input type="hidden" name="action" value="delete">
                                <input type="hidden" name="id" value="${c.id}">
                                <button type="submit" class="btn btn-danger btn-sm">Delete</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>

    <div class="stack">
        <div class="soft-panel">
            <h2 class="panel-title">Notes</h2>
            <ul class="summary-list">
                <li>Use clear names so users understand categories quickly.</li>
                <li>Delete only categories with no transactions.</li>
                <li>Keep income and expense types separate.</li>
            </ul>
        </div>
    </div>
</div>

<jsp:include page="/includes/footer.jsp"/>
