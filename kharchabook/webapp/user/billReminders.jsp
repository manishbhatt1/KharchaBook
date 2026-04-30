<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<c:set var="pageTitle" value="Bill Reminders" scope="request"/>
<jsp:include page="/includes/header.jsp"/>

<div class="page-hero">
    <div>
        <h1 class="page-title">Recurring bill reminders</h1>
        <p class="lead">Track rent, internet, electricity and other regular payments before late fees happen.</p>
    </div>
    <div class="hero-actions">
        <a href="${pageContext.request.contextPath}/user/dashboard" class="btn btn-secondary">Back to dashboard</a>
    </div>
</div>

<jsp:include page="/includes/flash.jsp"/>

<div class="layout-split">
    <div class="stack">
        <c:if test="${not empty editReminder}">
            <form method="post" action="${pageContext.request.contextPath}/user/bills" class="card">
                <h2 class="panel-title">Edit reminder</h2>
                <input type="hidden" name="action" value="update">
                <input type="hidden" name="id" value="${editReminder.id}">
                <div class="grid">
                    <div class="form-row">
                        <label>Bill name</label>
                        <input type="text" name="billName" value="${editReminder.billName}" required>
                    </div>
                    <div class="form-row">
                        <label>Amount (NPR)</label>
                        <input type="number" name="amount" step="0.01" min="0.01" value="${editReminder.amount}" required>
                    </div>
                    <div class="form-row">
                        <label>Due date</label>
                        <input type="date" name="dueDate" value="${editReminder.dueDate}" required>
                    </div>
                    <div class="form-row">
                        <label>Frequency</label>
                        <select name="frequency" required>
                            <option value="monthly" ${editReminder.frequency == 'monthly' ? 'selected' : ''}>Monthly</option>
                            <option value="yearly" ${editReminder.frequency == 'yearly' ? 'selected' : ''}>Yearly</option>
                        </select>
                    </div>
                    <div class="form-row">
                        <label>Status</label>
                        <select name="status">
                            <option value="active" ${editReminder.status == 'active' ? 'selected' : ''}>Active</option>
                            <option value="paused" ${editReminder.status == 'paused' ? 'selected' : ''}>Paused</option>
                        </select>
                    </div>
                    <div class="form-row">
                        <label>Notes</label>
                        <textarea name="notes" rows="3">${editReminder.notes}</textarea>
                    </div>
                </div>
                <button type="submit" class="btn btn-primary">Save reminder</button>
                <a href="${pageContext.request.contextPath}/user/bills" class="btn btn-secondary">Cancel</a>
            </form>
        </c:if>

        <c:if test="${empty editReminder}">
            <form method="post" action="${pageContext.request.contextPath}/user/bills" class="card">
                <h2 class="panel-title">Add reminder</h2>
                <input type="hidden" name="action" value="create">
                <div class="grid">
                    <div class="form-row">
                        <label>Bill name</label>
                        <input type="text" name="billName" placeholder="Rent, NTC, NEA..." required>
                    </div>
                    <div class="form-row">
                        <label>Amount (NPR)</label>
                        <input type="number" name="amount" step="0.01" min="0.01" required>
                    </div>
                    <div class="form-row">
                        <label>Due date</label>
                        <input type="date" name="dueDate" required>
                    </div>
                    <div class="form-row">
                        <label>Frequency</label>
                        <select name="frequency" required>
                            <option value="monthly">Monthly</option>
                            <option value="yearly">Yearly</option>
                        </select>
                    </div>
                    <div class="form-row">
                        <label>Notes</label>
                        <textarea name="notes" rows="3" placeholder="Optional reminder note"></textarea>
                    </div>
                </div>
                <button type="submit" class="btn btn-primary">Save reminder</button>
            </form>
        </c:if>

        <div class="soft-panel">
            <h2 class="panel-title">All reminders</h2>
            <p class="small-muted">Paused reminders stay saved but do not show as active.</p>
        </div>

        <div class="stack">
            <c:forEach var="b" items="${billReminders}">
                <div class="card">
                    <div class="page-hero" style="margin-bottom:0.5rem;align-items:flex-start">
                        <div>
                            <strong>${b.billName}</strong><br>
                            <span class="small-muted">Chosen date ${b.dueDate} | ${b.frequency} | NPR ${b.amount}</span>
                        </div>
                        <div class="role-pill">${b.status}</div>
                    </div>
                    <p class="small-muted">
                        Next due date:
                        <c:choose>
                            <c:when test="${not empty b.nextDueDate}">
                                ${b.nextDueDate} (${b.daysUntilDue} day(s) left)
                            </c:when>
                            <c:otherwise>-</c:otherwise>
                        </c:choose>
                    </p>
                    <p>${b.notes}</p>
                    <div class="btn-group">
                        <a class="btn btn-secondary btn-sm" href="${pageContext.request.contextPath}/user/bills?action=edit&id=${b.id}">Edit</a>
                        <form method="post" action="${pageContext.request.contextPath}/user/bills" style="display:inline" onsubmit="return confirm('Delete this reminder?');">
                            <input type="hidden" name="action" value="delete">
                            <input type="hidden" name="id" value="${b.id}">
                            <button type="submit" class="btn btn-danger btn-sm">Delete</button>
                        </form>
                        <form method="post" action="${pageContext.request.contextPath}/user/bills" style="display:inline">
                            <input type="hidden" name="action" value="toggle">
                            <input type="hidden" name="id" value="${b.id}">
                            <input type="hidden" name="status" value="${b.status}">
                            <button type="submit" class="btn btn-secondary btn-sm">${b.active ? 'Pause' : 'Activate'}</button>
                        </form>
                    </div>
                </div>
            </c:forEach>
            <c:if test="${empty billReminders}">
                <div class="soft-panel">
                    <p class="small-muted">No bill reminders added yet.</p>
                </div>
            </c:if>
        </div>
    </div>

    <div class="stack">
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
            <h2 class="panel-title">Why this helps</h2>
            <ul class="summary-list">
                <li>Shows upcoming regular costs before they become late fees.</li>
                <li>Useful for rent, internet, electricity and phone bills.</li>
                <li>Keeps recurring spending visible on the dashboard.</li>
            </ul>
        </div>
    </div>
</div>

<jsp:include page="/includes/footer.jsp"/>
