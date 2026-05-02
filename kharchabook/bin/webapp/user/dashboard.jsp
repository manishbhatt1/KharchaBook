<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions" %>
<c:set var="pageTitle" value="Dashboard" scope="request"/>
<jsp:include page="/includes/header.jsp"/>

<div class="page-hero">
    <div>
        <h1 class="page-title">Dashboard</h1>
        <p class="lead">A quick view of this month's income, expenses, savings goals and upcoming reminders.</p>
    </div>
    <div class="hero-actions">
        <a href="${pageContext.request.contextPath}/user/transactions?action=add" class="btn btn-primary">Add transaction</a>
        <a href="${pageContext.request.contextPath}/user/goals?action=new" class="btn btn-secondary">New goal</a>
        <a href="${pageContext.request.contextPath}/user/bills" class="btn btn-secondary">Bill reminders</a>
        <a href="${pageContext.request.contextPath}/user/remittance" class="btn btn-secondary">Allocate remittance</a>
    </div>
</div>

<jsp:include page="/includes/flash.jsp"/>

<c:if test="${not empty dueSoonBills}">
    <div class="dashboard-reminder-strip">
        <strong>Payment reminders:</strong>
        <c:forEach var="b" items="${dueSoonBills}" varStatus="loop">
            <span>
                ${b.billName} is due in ${b.daysUntilDue} day(s)
                <c:if test="${not loop.last}">|</c:if>
            </span>
        </c:forEach>
    </div>
</c:if>

<div class="grid">
    <div class="card">
        <h3>Income this month</h3>
        <div class="stat-value">NPR ${incomeMonth}</div>
    </div>
    <div class="card">
        <h3>Expenses this month</h3>
        <div class="stat-value">NPR ${expenseMonth}</div>
    </div>
    <div class="card">
        <h3>Net balance</h3>
        <div class="stat-value">NPR ${netBalance}</div>
    </div>
    <div class="card">
        <h3>Today's spending</h3>
        <div class="stat-value">NPR ${todayExpense} across ${todayCategoryCount} categories</div>
    </div>
    <div class="card">
        <h3>Top spending category</h3>
        <div class="stat-value" style="font-size:1.1rem">${topCategoryName}</div>
    </div>
    <div class="card">
        <h3>Set aside in goals</h3>
        <div class="stat-value">NPR ${reservedAmount}</div>
    </div>
    <div class="card">
        <h3>Available after reserve</h3>
        <div class="stat-value">NPR ${availableAfterReserve}</div>
    </div>
</div>

<div class="insight-box">
    <strong>Insight:</strong> ${dashboardInsight}
</div>

<div class="layout-split section-space">
    <div class="stack">
        <div class="soft-panel">
            <h2 class="panel-title">Recent transactions</h2>
            <p class="small-muted">Your latest five records are shown here.</p>
        </div>
        <div class="table-wrap">
            <table class="data">
                <thead>
                <tr><th>Date</th><th>Type</th><th>Category</th><th>Amount</th></tr>
                </thead>
                <tbody>
                <c:forEach var="t" items="${recentTransactions}">
                    <tr>
                        <td>${t.transactionDate}</td>
                        <td>${t.type}</td>
                        <td>${t.categoryName}</td>
                        <td>NPR ${t.amount}</td>
                    </tr>
                </c:forEach>
                <c:if test="${empty recentTransactions}">
                    <tr><td colspan="4">No transactions yet.</td></tr>
                </c:if>
                </tbody>
            </table>
        </div>

        <div class="soft-panel">
            <h2 class="panel-title">Expense breakdown this month</h2>
            <c:if test="${not empty expenseBreakdown}">
                <div class="bar-list">
                    <c:forEach var="item" items="${expenseBreakdown}">
                        <c:set var="barWidth" value="${item.percent}"/>
                        <c:if test="${item.percent > 100}">
                            <c:set var="barWidth" value="100"/>
                        </c:if>
                        <div class="bar-row">
                            <div class="bar-head">
                                <strong>${item.name}</strong>
                                <span>NPR ${item.amount} (<fmt:formatNumber value="${item.percent}" maxFractionDigits="0"/>%)</span>
                            </div>
                            <div class="trend-tag trend-${item.trendState}">${item.trendText}</div>
                            <div class="progress">
                                <div class="progress-bar" style="width: ${barWidth}%;"></div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:if>
            <c:if test="${empty expenseBreakdown}">
                <p class="small-muted">No expense records this month yet.</p>
            </c:if>
        </div>
    </div>

    <div class="stack">
        <div class="soft-panel">
            <h2 class="panel-title">Can I afford this?</h2>
            <p class="small-muted">Check a purchase before you spend so your budget and goals stay protected.</p>
            <form method="get" action="${pageContext.request.contextPath}/user/dashboard" class="afford-form">
                <div class="form-row">
                    <label>Purchase price (NPR)</label>
                    <input type="number" name="purchasePrice" step="0.01" min="0.01" value="${purchasePrice}" placeholder="e.g. 15000" required>
                </div>
                <button type="submit" class="btn btn-secondary btn-sm">Check affordability</button>
            </form>
            <c:if test="${not empty affordabilityError}">
                <div class="alert alert-danger">${affordabilityError}</div>
            </c:if>
            <c:if test="${not empty affordabilityMessage}">
                <div class="alert alert-${affordabilityStatus}">${affordabilityMessage}</div>
            </c:if>
        </div>

        <div class="soft-panel">
            <h2 class="panel-title">Bills due this week</h2>
            <c:if test="${not empty dueSoonBills}">
                <ul class="summary-list">
                    <c:forEach var="b" items="${dueSoonBills}">
                        <li>
                            <strong>${b.billName}</strong><br>
                            Due on ${b.nextDueDate} in ${b.daysUntilDue} day(s) | NPR ${b.amount}
                        </li>
                    </c:forEach>
                </ul>
            </c:if>
            <c:if test="${empty dueSoonBills}">
                <p class="small-muted">No active bills are due in the next 7 days.</p>
            </c:if>
        </div>

        <div class="soft-panel">
            <h2 class="panel-title">Upcoming reminders</h2>
            <c:if test="${not empty dueSoonGoals}">
                <ul class="summary-list">
                    <c:forEach var="g" items="${dueSoonGoals}">
                        <li>
                            <strong>${g.title}</strong><br>
                            Due in ${g.daysUntilDeadline} day(s) | Remaining NPR ${g.remainingAmount}
                        </li>
                    </c:forEach>
                </ul>
            </c:if>
            <c:if test="${empty dueSoonGoals}">
                <p class="small-muted">No active goals are due in the next 14 days.</p>
            </c:if>
        </div>

        <div class="soft-panel">
            <h3 class="panel-title">Money set aside</h3>
            <p class="small-muted">Use savings goals for school fees, rent, emergencies, or festival costs. Adding money to a goal helps you protect that amount from daily spending.</p>
            <div class="mini-grid">
                <div class="metric-card">
                    <strong>${activeGoalCount}</strong>
                    <span>Active goals</span>
                </div>
                <div class="metric-card">
                    <strong>NPR ${reservedAmount}</strong>
                    <span>Currently reserved</span>
                </div>
            </div>
        </div>

        <div class="soft-panel">
            <h3 class="panel-title">Good next steps</h3>
            <ul class="action-list">
                <li><a href="${pageContext.request.contextPath}/user/goals?action=new">Create a goal for school fees or bills</a></li>
                <li><a href="${pageContext.request.contextPath}/user/budgets">Review monthly budget limits</a></li>
                <li><a href="${pageContext.request.contextPath}/user/tips">Read a savings or budgeting tip</a></li>
            </ul>
        </div>
    </div>
</div>

<c:if test="${not empty dueSoonBills}">
    <div class="modal-backdrop" id="billReminderPopup" style="display:none">
        <div class="modal-card" role="dialog" aria-modal="true" aria-labelledby="billReminderTitle">
            <div class="modal-head">
                <div>
                    <h2 id="billReminderTitle" class="panel-title">Upcoming bill payments</h2>
                    <p class="small-muted">These payments are due soon, so the dashboard is reminding you now.</p>
                </div>
                <button type="button" class="btn btn-secondary btn-sm" id="closeBillReminderPopup">Close</button>
            </div>
            <ul class="summary-list">
                <c:forEach var="b" items="${dueSoonBills}">
                    <li>
                        <strong>${b.billName}</strong><br>
                        Pay by ${b.nextDueDate} in ${b.daysUntilDue} day(s) | NPR ${b.amount}
                    </li>
                </c:forEach>
            </ul>
            <div class="btn-group" style="margin-top:1rem">
                <a href="${pageContext.request.contextPath}/user/bills" class="btn btn-primary">Manage reminders</a>
            </div>
        </div>
    </div>
    <script>
        (function () {
            var popup = document.getElementById('billReminderPopup');
            var closeButton = document.getElementById('closeBillReminderPopup');
            if (!popup || !closeButton) {
                return;
            }

            var popupKey = 'billReminderPopup_dismissed';
            if (window.localStorage.getItem(popupKey) === 'dismissed') {
                return;
            }

            popup.style.display = 'flex';

            function closeBillPopup() {
                popup.hidden = true;
                popup.style.display = 'none';
                window.localStorage.setItem(popupKey, 'dismissed');
            }

            closeButton.addEventListener('click', closeBillPopup);
            popup.addEventListener('click', function (event) {
                if (event.target === popup) {
                    closeBillPopup();
                }
            });
        }());
    </script>
</c:if>

<c:if test="${not empty urgentFees}">
    <div class="modal-backdrop" id="feeWarningPopup" hidden>
        <div class="modal-card" role="dialog" aria-modal="true" aria-labelledby="feeWarningTitle">
            <div class="modal-head">
                <div>
                    <h2 id="feeWarningTitle" class="panel-title">⚠️ Fee Payment Warning</h2>
                    <p class="small-muted">Your net balance is below the required fee amount. Please avoid spending more money.</p>
                </div>
                <button type="button" class="btn btn-secondary btn-sm" id="closeFeeWarningPopup">Close</button>
            </div>
            <div class="alert alert-danger" style="margin: 1rem 0;">
                <strong>⚠️ Warning:</strong> Your current net balance (NPR ${netBalance}) is insufficient to cover upcoming fees.
            </div>
            <ul class="summary-list">
                <c:forEach var="fee" items="${urgentFees}">
                    <li>
                        <strong>${fee.feeName}</strong><br>
                        Due: ${fee.dueDate} (${fee.daysUntilDue} days) | Required: NPR ${fee.amount}<br>
                        <span style="color: #dc3545;">❌ Shortfall: NPR ${fee.amount - netBalance}</span>
                    </li>
                </c:forEach>
            </ul>
            <div class="alert alert-info" style="margin: 1rem 0;">
                <strong>💡 Recommendation:</strong> Please postpone non-essential expenses until you have sufficient funds for these fees.
            </div>
            <div class="btn-group" style="margin-top:1rem">
                <a href="${pageContext.request.contextPath}/user/fees" class="btn btn-primary">Manage Fees</a>
                <a href="${pageContext.request.contextPath}/user/transactions" class="btn btn-secondary">Review Spending</a>
            </div>
        </div>
    </div>
    <script>
        (function () {
            var popup = document.getElementById('feeWarningPopup');
            var closeButton = document.getElementById('closeFeeWarningPopup');
            if (!popup || !closeButton) {
                return;
            }

            var popupKey = '${fn:escapeXml(feeWarningPopupKey)}';
            if (window.localStorage.getItem(popupKey) === 'dismissed') {
                return;
            }

            popup.hidden = false;

            function closeFeePopup() {
                popup.hidden = true;
                popup.style.display = 'none';
                window.localStorage.setItem(popupKey, 'dismissed');
            }

            closeButton.addEventListener('click', closeFeePopup);
            popup.addEventListener('click', function (event) {
                if (event.target === popup) {
                    closeFeePopup();
                }
            });
        }());
    </script>
</c:if>

<jsp:include page="/includes/footer.jsp"/>
