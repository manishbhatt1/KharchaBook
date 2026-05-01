<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!-- Set page title -->
<c:set var="pageTitle" value="Reports" scope="request"/>

<!-- Include common header -->
<jsp:include page="/includes/header.jsp"/>

<!-- =========================
     PAGE HEADER
========================= -->
<div class="page-hero">
    <div>
        <h1 class="page-title">Reports and analytics</h1>
        <p class="lead">
            Review monthly totals, category breakdown and the last six months in one place.
        </p>
    </div>
</div>

<div class="layout-split">

    <!-- =========================
         LEFT SIDE (REPORT TABLES)
    ========================= -->
    <div class="stack">

        <!-- Year filter form -->
        <form method="get" action="${pageContext.request.contextPath}/user/reports" class="card inline-form">
            <div class="form-row">
                <label>Year for monthly table</label>
                <!-- User selects year -->
                <input type="number" name="year" value="${reportYear}" min="2000" max="2100">
            </div>
            <button type="submit" class="btn btn-secondary">Apply</button>
        </form>

        <!-- =========================
             MONTHLY REPORT TABLE
        ========================= -->
        <div class="table-wrap">
            <table class="data">
                <thead>
                <tr>
                    <th>Month</th>
                    <th>Income</th>
                    <th>Expenses</th>
                    <th>Net</th>
                </tr>
                </thead>
                <tbody>
                <!-- Loop through monthly data -->
                <c:forEach var="row" items="${monthlyRows}">
                    <tr>
                        <td>${row[0]}</td>
                        <td>NPR ${row[1]}</td>
                        <td>NPR ${row[2]}</td>
                        <td>NPR ${row[3]}</td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>

        <!-- =========================
             CATEGORY BREAKDOWN TABLE
        ========================= -->
        <div class="table-wrap">
            <table class="data">
                <thead>
                <tr>
                    <th>Category</th>
                    <th>Amount</th>
                    <th>% of expenses</th>
                </tr>
                </thead>
                <tbody>
                <!-- Loop through category data -->
                <c:forEach var="r" items="${categoryBreakdown}">
                    <tr>
                        <td>${r.name}</td>
                        <td>NPR ${r.amount}</td>
                        <td>${r.percent}%</td>
                    </tr>
                </c:forEach>

                <!-- If no data -->
                <c:if test="${empty categoryBreakdown}">
                    <tr>
                        <td colspan="3">No expense data this month.</td>
                    </tr>
                </c:if>
                </tbody>
            </table>
        </div>

        <!-- =========================
             LAST 6 MONTHS TABLE
        ========================= -->
        <div class="table-wrap">
            <table class="data">
                <thead>
                <tr>
                    <th>Year</th>
                    <th>Month</th>
                    <th>Income</th>
                    <th>Expenses</th>
                </tr>
                </thead>
                <tbody>
                <!-- Loop through last 6 months data -->
                <c:forEach var="r" items="${last6Months}">
                    <tr>
                        <td>${r[0]}</td>
                        <td>${r[1]}</td>
                        <td>NPR ${r[2]}</td>
                        <td>NPR ${r[3]}</td>
                    </tr>
                </c:forEach>
                </tbody>
            </table>
        </div>
    </div>

    <!-- =========================
         RIGHT SIDE (HIGHLIGHTS)
    ========================= -->
    <div class="stack">
        <div class="soft-panel">
            <h2 class="panel-title">Highlights</h2>

            <!-- Summary insights -->
            <ul class="summary-list">
                <li>
                    Highest spending month in ${reportYear}: Month ${highestExpenseMonth}
                </li>
                <li>
                    Highest expense amount: NPR ${highestExpenseAmount}
                </li>
                <li>
                    Current month breakdown: ${breakdownMonth} / ${breakdownYear}
                </li>
            </ul>
        </div>
    </div>
</div>

<!-- Include footer -->
<jsp:include page="/includes/footer.jsp"/>