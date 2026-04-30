<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Add transaction" scope="request"/>
<jsp:include page="/includes/header.jsp"/>
<h1 class="page-title">Add income or expense</h1>
<jsp:include page="/includes/flash.jsp"/>
<form method="post" action="${pageContext.request.contextPath}/user/transactions">
    <input type="hidden" name="action" value="add">
    <div class="form-row">
        <label>Type</label>
        <select name="type" id="typeSel" required>
            <option value="income">Income</option>
            <option value="expense">Expense</option>
        </select>
    </div>
    <div class="form-row">
        <label>Category</label>
        <select name="categoryId" id="catIncome" style="display:block">
            <c:forEach var="c" items="${incomeCategories}">
                <option value="${c.id}">${c.name}</option>
            </c:forEach>
        </select>
        <select id="catExpense" style="display:none">
            <c:forEach var="c" items="${expenseCategories}">
                <option value="${c.id}">${c.name}</option>
            </c:forEach>
        </select>
    </div>
    <div class="form-row">
        <label>Amount (NPR)</label>
        <input type="number" name="amount" step="0.01" min="0.01" required>
    </div>
    <div class="form-row">
        <label>Date</label>
        <input type="date" name="transactionDate" required>
    </div>
    <div class="form-row">
        <label>Description (optional)</label>
        <input type="text" name="description" maxlength="500">
    </div>
    <button type="submit" class="btn btn-primary">Save</button>
    <a href="${pageContext.request.contextPath}/user/transactions" class="btn btn-secondary">Cancel</a>
</form>
<script>
document.getElementById('typeSel').addEventListener('change', function() {
    var inc = document.getElementById('catIncome');
    var exp = document.getElementById('catExpense');
    if (this.value === 'income') {
        inc.style.display='block'; inc.setAttribute('name','categoryId'); exp.style.display='none'; exp.removeAttribute('name');
    } else {
        exp.style.display='block'; exp.setAttribute('name','categoryId'); inc.style.display='none'; inc.removeAttribute('name');
    }
});
</script>
<jsp:include page="/includes/footer.jsp"/>
