<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="pageTitle" value="Edit transaction" scope="request"/>
<jsp:include page="/includes/header.jsp"/>
<h1 class="page-title">Edit transaction</h1>
<jsp:include page="/includes/flash.jsp"/>
<form method="post" action="${pageContext.request.contextPath}/user/transactions">
    <input type="hidden" name="action" value="update">
    <input type="hidden" name="id" value="${tx.id}">
    <div class="form-row">
        <label>Type</label>
        <select name="type" id="typeSel" required>
            <option value="income" ${tx.type == 'income' ? 'selected' : ''}>Income</option>
            <option value="expense" ${tx.type == 'expense' ? 'selected' : ''}>Expense</option>
        </select>
    </div>
    <div class="form-row">
        <label>Category</label>
        <select id="catIncome" style="${tx.type == 'income' ? '' : 'display:none'}">
            <c:forEach var="c" items="${incomeCategories}">
                <option value="${c.id}" ${c.id == tx.categoryId ? 'selected' : ''}>${c.name}</option>
            </c:forEach>
        </select>
        <select id="catExpense" style="${tx.type == 'expense' ? '' : 'display:none'}">
            <c:forEach var="c" items="${expenseCategories}">
                <option value="${c.id}" ${c.id == tx.categoryId ? 'selected' : ''}>${c.name}</option>
            </c:forEach>
        </select>
        <input type="hidden" name="categoryId" id="catHidden" value="${tx.categoryId}">
    </div>
    <div class="form-row">
        <label>Amount (NPR)</label>
        <input type="number" name="amount" step="0.01" min="0.01" value="${tx.amount}" required>
    </div>
    <div class="form-row">
        <label>Date</label>
        <input type="date" name="transactionDate" value="${tx.transactionDate}" required>
    </div>
    <div class="form-row">
        <label>Description</label>
        <input type="text" name="description" value="${tx.description}" maxlength="500">
    </div>
    <button type="submit" class="btn btn-primary">Update</button>
    <a href="${pageContext.request.contextPath}/user/transactions" class="btn btn-secondary">Cancel</a>
</form>
<script>
(function(){
    var typeSel = document.getElementById('typeSel');
    var inc = document.getElementById('catIncome');
    var exp = document.getElementById('catExpense');
    var hid = document.getElementById('catHidden');
    function sync() {
        if (typeSel.value === 'income') {
            inc.style.display='block'; exp.style.display='none';
            hid.value = inc.value;
        } else {
            exp.style.display='block'; inc.style.display='none';
            hid.value = exp.value;
        }
    }
    typeSel.addEventListener('change', sync);
    inc.addEventListener('change', function(){ hid.value = inc.value; });
    exp.addEventListener('change', function(){ hid.value = exp.value; });
    sync();
})();
</script>
<jsp:include page="/includes/footer.jsp"/>
