<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

<!-- JSTL core & formatting libraries -->
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">

    <!-- Responsive design -->
    <meta name="viewport" content="width=device-width, initial-scale=1.0">

    <title>Remittance Allocation - Kharcha Book</title>

    <!-- Font Awesome icons -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">

    <style>
        /* =========================
           GLOBAL STYLES
        ========================= */
        * { margin: 0; padding: 0; box-sizing: border-box; }

        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;

            /* Gradient background */
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        }

        /* Main container */
        .container {
            max-width: 1200px;
            margin: 0 auto;
            background: white;
            border-radius: 15px;
        }

        /* =========================
           HEADER
        ========================= */
        .header {
            background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
            color: white;
            text-align: center;
        }

        /* =========================
           NAVIGATION TABS
        ========================= */
        .nav-tabs {
            display: flex;
            background: #f8f9fa;
        }

        .nav-tab.active {
            background: white;
            color: #4facfe;
        }

        /* =========================
           FORM & INPUTS
        ========================= */
        .form-group input, .form-group textarea {
            border: 2px solid #e9ecef;
        }

        /* =========================
           ALLOCATION BREAKDOWN
        ========================= */
        .amount-breakdown {
            background: white;
            border-radius: 10px;
        }

        /* =========================
           BUTTONS
        ========================= */
        .btn {
            cursor: pointer;
            transition: 0.3s;
        }

        .btn-primary {
            background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
        }

        /* =========================
           CARDS (HISTORY)
        ========================= */
        .allocation-card {
            border: 2px solid #e9ecef;
        }

        /* =========================
           ALERT MESSAGES
        ========================= */
        .alert-success { background: #d4edda; }
        .alert-danger { background: #f8d7da; }
    </style>
</head>

<body>

<!-- =========================
     MAIN CONTAINER
========================= -->
<div class="container">

    <!-- HEADER -->
    <div class="header">
        <h1><i class="fas fa-hand-holding-usd"></i> Remittance Allocation Planner</h1>
        <p>Split your remittance immediately to avoid overspending</p>
    </div>

    <!-- NAVIGATION TABS -->
    <div class="nav-tabs">
        <button class="nav-tab active" onclick="showTab('allocate')">
            Allocate Remittance
        </button>
        <button class="nav-tab" onclick="showTab('history')">
            Allocation History
        </button>
    </div>

    <div class="content">

        <!-- =========================
             FLASH MESSAGES
        ========================= -->
        <c:if test="${not empty flash_success}">
            <div class="alert alert-success">${flash_success}</div>
        </c:if>

        <c:if test="${not empty flash_error}">
            <div class="alert alert-danger">${flash_error}</div>
        </c:if>

        <!-- =========================
             ALLOCATION FORM TAB
        ========================= -->
        <div id="allocate-tab">

            <!-- Monthly summary -->
            <div class="month-summary">
                <h3>This Month's Total</h3>
                <div class="month-total">
                    <fmt:formatNumber value="${monthRemittanceTotal}" type="currency" currencyCode="NPR"/>
                </div>
            </div>

            <!-- Allocation Form -->
            <form action="remittance" method="post">
                <input type="hidden" name="action" value="allocate">

                <!-- Total amount input -->
                <input type="number" id="totalAmount" name="totalAmount"
                       onchange="calculateAllocation()">

                <!-- Allocation categories -->
                <input type="number" id="rentAmount" onchange="calculateAllocation()">
                <input type="number" id="foodAmount" onchange="calculateAllocation()">
                <input type="number" id="savingsAmount" onchange="calculateAllocation()">
                <input type="number" id="otherAmount" onchange="calculateAllocation()">

                <!-- Live breakdown display -->
                <div class="amount-breakdown">
                    <span id="total-display"></span>
                    <span id="allocated-display"></span>
                    <span id="unallocated-display"></span>
                </div>

                <!-- Submit -->
                <button type="submit" class="btn btn-primary">
                    Allocate
                </button>
            </form>
        </div>

        <!-- =========================
             HISTORY TAB
        ========================= -->
        <div id="history-tab" style="display:none;">

            <!-- If no data -->
            <c:if test="${empty remittanceAllocations}">
                <p>No allocations yet</p>
            </c:if>

            <!-- Loop allocations -->
            <c:forEach items="${remittanceAllocations}" var="allocation">
                <div class="allocation-card">

                    <!-- Date + Amount -->
                    <fmt:formatDate value="${allocation.allocationDate}"/>
                    <fmt:formatNumber value="${allocation.totalAmount}"/>

                    <!-- Breakdown -->
                    <fmt:formatNumber value="${allocation.rentAmount}"/>
                    <fmt:formatNumber value="${allocation.foodAmount}"/>

                    <!-- Actions -->
                    <a href="remittance?action=edit&id=${allocation.id}">Edit</a>
                    <a href="remittance?action=delete&id=${allocation.id}">Delete</a>
                </div>
            </c:forEach>
        </div>

    </div>
</div>

<!-- =========================
     JAVASCRIPT
========================= -->
<script>
    // Switch tabs (Allocate / History)
    function showTab(tabName) {
        document.getElementById('allocate-tab').style.display =
            tabName === 'allocate' ? 'block' : 'none';

        document.getElementById('history-tab').style.display =
            tabName === 'history' ? 'block' : 'none';
    }

    // Calculate allocation in real-time
    function calculateAllocation() {
        const total = parseFloat(document.getElementById('totalAmount').value) || 0;

        const rent = parseFloat(document.getElementById('rentAmount').value) || 0;
        const food = parseFloat(document.getElementById('foodAmount').value) || 0;
        const savings = parseFloat(document.getElementById('savingsAmount').value) || 0;
        const other = parseFloat(document.getElementById('otherAmount').value) || 0;

        const allocated = rent + food + savings + other;
        const remaining = total - allocated;

        // Update UI
        document.getElementById('allocated-display').textContent = allocated;
        document.getElementById('unallocated-display').textContent = remaining;
    }
</script>

</body>
</html>