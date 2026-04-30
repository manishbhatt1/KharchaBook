<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Remittance Allocation - Kharcha Book</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            min-height: 100vh;
            padding: 20px;
        }
        
        .container {
            max-width: 1200px;
            margin: 0 auto;
            background: white;
            border-radius: 15px;
            box-shadow: 0 20px 40px rgba(0,0,0,0.1);
            overflow: hidden;
        }
        
        .header {
            background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
            color: white;
            padding: 30px;
            text-align: center;
        }
        
        .header h1 {
            font-size: 2.5em;
            margin-bottom: 10px;
        }
        
        .header p {
            font-size: 1.1em;
            opacity: 0.9;
        }
        
        .nav-tabs {
            display: flex;
            background: #f8f9fa;
            border-bottom: 2px solid #e9ecef;
        }
        
        .nav-tab {
            flex: 1;
            padding: 15px;
            text-align: center;
            background: none;
            border: none;
            cursor: pointer;
            font-size: 1em;
            transition: all 0.3s ease;
        }
        
        .nav-tab.active {
            background: white;
            color: #4facfe;
            border-bottom: 3px solid #4facfe;
        }
        
        .nav-tab:hover {
            background: #e9ecef;
        }
        
        .content {
            padding: 30px;
        }
        
        .allocation-form {
            background: #f8f9fa;
            padding: 25px;
            border-radius: 10px;
            margin-bottom: 30px;
        }
        
        .form-grid {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
            gap: 20px;
            margin-bottom: 20px;
        }
        
        .form-group {
            display: flex;
            flex-direction: column;
        }
        
        .form-group label {
            font-weight: 600;
            margin-bottom: 8px;
            color: #495057;
        }
        
        .form-group input, .form-group textarea {
            padding: 12px;
            border: 2px solid #e9ecef;
            border-radius: 8px;
            font-size: 1em;
            transition: border-color 0.3s ease;
        }
        
        .form-group input:focus, .form-group textarea:focus {
            outline: none;
            border-color: #4facfe;
        }
        
        .amount-breakdown {
            background: white;
            padding: 20px;
            border-radius: 10px;
            margin: 20px 0;
            border: 2px solid #e9ecef;
        }
        
        .amount-row {
            display: flex;
            justify-content: space-between;
            align-items: center;
            padding: 10px 0;
            border-bottom: 1px solid #f1f3f4;
        }
        
        .amount-row:last-child {
            border-bottom: none;
            font-weight: 700;
            color: #4facfe;
        }
        
        .btn {
            padding: 12px 24px;
            border: none;
            border-radius: 8px;
            font-size: 1em;
            cursor: pointer;
            transition: all 0.3s ease;
            text-decoration: none;
            display: inline-block;
            margin: 5px;
        }
        
        .btn-primary {
            background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
            color: white;
        }
        
        .btn-secondary {
            background: #6c757d;
            color: white;
        }
        
        .btn-danger {
            background: #dc3545;
            color: white;
        }
        
        .btn:hover {
            transform: translateY(-2px);
            box-shadow: 0 5px 15px rgba(0,0,0,0.2);
        }
        
        .allocations-list {
            margin-top: 30px;
        }
        
        .allocation-card {
            background: white;
            border: 2px solid #e9ecef;
            border-radius: 10px;
            padding: 20px;
            margin-bottom: 15px;
            transition: all 0.3s ease;
        }
        
        .allocation-card:hover {
            box-shadow: 0 5px 15px rgba(0,0,0,0.1);
            transform: translateY(-2px);
        }
        
        .allocation-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 15px;
        }
        
        .allocation-date {
            color: #6c757d;
            font-size: 0.9em;
        }
        
        .allocation-amount {
            font-size: 1.3em;
            font-weight: 700;
            color: #4facfe;
        }
        
        .allocation-details {
            display: grid;
            grid-template-columns: repeat(auto-fit, minmax(150px, 1fr));
            gap: 15px;
            margin: 15px 0;
        }
        
        .detail-item {
            text-align: center;
            padding: 10px;
            background: #f8f9fa;
            border-radius: 8px;
        }
        
        .detail-label {
            font-size: 0.8em;
            color: #6c757d;
            margin-bottom: 5px;
        }
        
        .detail-value {
            font-weight: 600;
            color: #495057;
        }
        
        .alert {
            padding: 15px;
            border-radius: 8px;
            margin-bottom: 20px;
        }
        
        .alert-success {
            background: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }
        
        .alert-danger {
            background: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
        
        .empty-state {
            text-align: center;
            padding: 40px;
            color: #6c757d;
        }
        
        .empty-state i {
            font-size: 4em;
            margin-bottom: 20px;
            opacity: 0.5;
        }
        
        .month-summary {
            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
            color: white;
            padding: 20px;
            border-radius: 10px;
            margin-bottom: 20px;
            text-align: center;
        }
        
        .month-summary h3 {
            margin-bottom: 10px;
        }
        
        .month-total {
            font-size: 2em;
            font-weight: 700;
        }
    </style>
</head>
<body>
    <div class="container">
        <div class="header">
            <h1><i class="fas fa-hand-holding-usd"></i> Remittance Allocation Planner</h1>
            <p>Split your remittance immediately to protect your money from overspending</p>
        </div>
        
        <div class="nav-tabs">
            <button class="nav-tab active" onclick="showTab('allocate')">
                <i class="fas fa-plus-circle"></i> Allocate Remittance
            </button>
            <button class="nav-tab" onclick="showTab('history')">
                <i class="fas fa-history"></i> Allocation History
            </button>
        </div>
        
        <div class="content">
            <c:if test="${not empty flash_success}">
                <div class="alert alert-success">
                    <i class="fas fa-check-circle"></i> ${flash_success}
                </div>
            </c:if>
            
            <c:if test="${not empty flash_error}">
                <div class="alert alert-danger">
                    <i class="fas fa-exclamation-circle"></i> ${flash_error}
                </div>
            </c:if>
            
            <div id="allocate-tab">
                <div class="month-summary">
                    <h3>This Month's Total Remittance</h3>
                    <div class="month-total">
                        <fmt:formatNumber value="${monthRemittanceTotal}" type="currency" currencyCode="NPR"/>
                    </div>
                </div>
                
                <div class="allocation-form">
                    <h3><i class="fas fa-calculator"></i> Allocate Your Remittance</h3>
                    <form action="remittance" method="post">
                        <input type="hidden" name="action" value="allocate">
                        
                        <div class="form-grid">
                            <div class="form-group">
                                <label for="totalAmount"><i class="fas fa-money-bill-wave"></i> Total Remittance Amount *</label>
                                <input type="number" id="totalAmount" name="totalAmount" step="0.01" min="0" required 
                                       placeholder="Enter total amount received" onchange="calculateAllocation()">
                            </div>
                            
                            <div class="form-group">
                                <label for="allocationDate"><i class="fas fa-calendar"></i> Allocation Date</label>
                                <input type="date" id="allocationDate" name="allocationDate" 
                                       value="<fmt:formatDate value="${today}" pattern="yyyy-MM-dd"/>">
                            </div>
                        </div>
                        
                        <h4 style="margin: 20px 0 15px; color: #495057;">
                            <i class="fas fa-divide"></i> How much for each category?
                        </h4>
                        
                        <div class="form-grid">
                            <div class="form-group">
                                <label for="rentAmount"><i class="fas fa-home"></i> Rent Amount</label>
                                <input type="number" id="rentAmount" name="rentAmount" step="0.01" min="0" 
                                       placeholder="0.00" onchange="calculateAllocation()">
                            </div>
                            
                            <div class="form-group">
                                <label for="foodAmount"><i class="fas fa-utensils"></i> Food Amount</label>
                                <input type="number" id="foodAmount" name="foodAmount" step="0.01" min="0" 
                                       placeholder="0.00" onchange="calculateAllocation()">
                            </div>
                            
                            <div class="form-group">
                                <label for="savingsAmount"><i class="fas fa-piggy-bank"></i> Savings Amount</label>
                                <input type="number" id="savingsAmount" name="savingsAmount" step="0.01" min="0" 
                                       placeholder="0.00" onchange="calculateAllocation()">
                            </div>
                            
                            <div class="form-group">
                                <label for="otherAmount"><i class="fas fa-ellipsis-h"></i> Other Amount</label>
                                <input type="number" id="otherAmount" name="otherAmount" step="0.01" min="0" 
                                       placeholder="0.00" onchange="calculateAllocation()">
                            </div>
                        </div>
                        
                        <div class="amount-breakdown">
                            <div class="amount-row">
                                <span>Total Remittance:</span>
                                <span id="total-display">NPR 0.00</span>
                            </div>
                            <div class="amount-row">
                                <span>Rent Allocation:</span>
                                <span id="rent-display">NPR 0.00</span>
                            </div>
                            <div class="amount-row">
                                <span>Food Allocation:</span>
                                <span id="food-display">NPR 0.00</span>
                            </div>
                            <div class="amount-row">
                                <span>Savings Allocation:</span>
                                <span id="savings-display">NPR 0.00</span>
                            </div>
                            <div class="amount-row">
                                <span>Other Allocation:</span>
                                <span id="other-display">NPR 0.00</span>
                            </div>
                            <div class="amount-row">
                                <span>Total Allocated:</span>
                                <span id="allocated-display">NPR 0.00</span>
                            </div>
                            <div class="amount-row">
                                <span>Unallocated:</span>
                                <span id="unallocated-display" style="color: #dc3545;">NPR 0.00</span>
                            </div>
                        </div>
                        
                        <div class="form-group">
                            <label for="description"><i class="fas fa-sticky-note"></i> Description (Optional)</label>
                            <textarea id="description" name="description" rows="3" 
                                      placeholder="Add any notes about this remittance allocation..."></textarea>
                        </div>
                        
                        <div style="text-align: center; margin-top: 20px;">
                            <button type="submit" class="btn btn-primary">
                                <i class="fas fa-save"></i> Allocate My Remittance
                            </button>
                        </div>
                    </form>
                </div>
            </div>
            
            <div id="history-tab" style="display: none;">
                <div class="allocations-list">
                    <h3><i class="fas fa-list"></i> Your Allocation History</h3>
                    
                    <c:choose>
                        <c:when test="${empty remittanceAllocations}">
                            <div class="empty-state">
                                <i class="fas fa-inbox"></i>
                                <h4>No Remittance Allocations Yet</h4>
                                <p>Start by allocating your first remittance to protect your money from overspending.</p>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <c:forEach items="${remittanceAllocations}" var="allocation">
                                <div class="allocation-card">
                                    <div class="allocation-header">
                                        <div>
                                            <div class="allocation-date">
                                                <i class="fas fa-calendar"></i>
                                                <fmt:formatDate value="${allocation.allocationDate}" pattern="MMMM dd, yyyy"/>
                                            </div>
                                            <div class="allocation-amount">
                                                <fmt:formatNumber value="${allocation.totalAmount}" type="currency" currencyCode="NPR"/>
                                            </div>
                                        </div>
                                        <div>
                                            <span style="padding: 5px 10px; background: ${allocation.status == 'active' ? '#d4edda' : '#f8d7da'}; 
                                                         color: ${allocation.status == 'active' ? '#155724' : '#721c24'}; 
                                                         border-radius: 15px; font-size: 0.8em;">
                                                ${allocation.status.toUpperCase()}
                                            </span>
                                        </div>
                                    </div>
                                    
                                    <c:if test="${not empty allocation.description}">
                                        <p style="color: #6c757d; margin-bottom: 15px;">${allocation.description}</p>
                                    </c:if>
                                    
                                    <div class="allocation-details">
                                        <div class="detail-item">
                                            <div class="detail-label"><i class="fas fa-home"></i> Rent</div>
                                            <div class="detail-value">
                                                <fmt:formatNumber value="${allocation.rentAmount}" type="currency" currencyCode="NPR"/>
                                            </div>
                                        </div>
                                        <div class="detail-item">
                                            <div class="detail-label"><i class="fas fa-utensils"></i> Food</div>
                                            <div class="detail-value">
                                                <fmt:formatNumber value="${allocation.foodAmount}" type="currency" currencyCode="NPR"/>
                                            </div>
                                        </div>
                                        <div class="detail-item">
                                            <div class="detail-label"><i class="fas fa-piggy-bank"></i> Savings</div>
                                            <div class="detail-value">
                                                <fmt:formatNumber value="${allocation.savingsAmount}" type="currency" currencyCode="NPR"/>
                                            </div>
                                        </div>
                                        <div class="detail-item">
                                            <div class="detail-label"><i class="fas fa-ellipsis-h"></i> Other</div>
                                            <div class="detail-value">
                                                <fmt:formatNumber value="${allocation.otherAmount}" type="currency" currencyCode="NPR"/>
                                            </div>
                                        </div>
                                    </div>
                                    
                                    <div style="text-align: right; margin-top: 15px;">
                                        <a href="remittance?action=edit&id=${allocation.id}" class="btn btn-secondary">
                                            <i class="fas fa-edit"></i> Edit
                                        </a>
                                        <a href="remittance?action=delete&id=${allocation.id}" 
                                           class="btn btn-danger" 
                                           onclick="return confirm('Are you sure you want to delete this allocation?')">
                                            <i class="fas fa-trash"></i> Delete
                                        </a>
                                    </div>
                                </div>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </div>
    
    <script>
        function showTab(tabName) {
            const tabs = document.querySelectorAll('.nav-tab');
            const allocateTab = document.getElementById('allocate-tab');
            const historyTab = document.getElementById('history-tab');
            
            tabs.forEach(tab => tab.classList.remove('active'));
            
            if (tabName === 'allocate') {
                tabs[0].classList.add('active');
                allocateTab.style.display = 'block';
                historyTab.style.display = 'none';
            } else {
                tabs[1].classList.add('active');
                allocateTab.style.display = 'none';
                historyTab.style.display = 'block';
            }
        }
        
        function calculateAllocation() {
            const total = parseFloat(document.getElementById('totalAmount').value) || 0;
            const rent = parseFloat(document.getElementById('rentAmount').value) || 0;
            const food = parseFloat(document.getElementById('foodAmount').value) || 0;
            const savings = parseFloat(document.getElementById('savingsAmount').value) || 0;
            const other = parseFloat(document.getElementById('otherAmount').value) || 0;
            
            const allocated = rent + food + savings + other;
            const unallocated = total - allocated;
            
            document.getElementById('total-display').textContent = 'NPR ' + total.toFixed(2);
            document.getElementById('rent-display').textContent = 'NPR ' + rent.toFixed(2);
            document.getElementById('food-display').textContent = 'NPR ' + food.toFixed(2);
            document.getElementById('savings-display').textContent = 'NPR ' + savings.toFixed(2);
            document.getElementById('other-display').textContent = 'NPR ' + other.toFixed(2);
            document.getElementById('allocated-display').textContent = 'NPR ' + allocated.toFixed(2);
            
            const unallocatedElement = document.getElementById('unallocated-display');
            unallocatedElement.textContent = 'NPR ' + Math.abs(unallocated).toFixed(2);
            unallocatedElement.style.color = unallocated < 0 ? '#dc3545' : '#28a745';
        }
        
        // Initialize calculation on page load
        calculateAllocation();
    </script>
</body>
</html>
