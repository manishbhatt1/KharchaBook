package com.kharchabook.servlet;

import com.kharchabook.dao.BillReminderDAO;
import com.kharchabook.dao.BudgetDAO;
import com.kharchabook.dao.CategoryDAO;
import com.kharchabook.dao.FeeDAO;
import com.kharchabook.dao.SavingsGoalDAO;
import com.kharchabook.dao.TransactionDAO;
import com.kharchabook.model.BillReminder;
import com.kharchabook.model.Budget;
import com.kharchabook.model.Fee;
import com.kharchabook.model.SavingsGoal;
import com.kharchabook.model.TransactionRecord;
import com.kharchabook.util.SessionKeys;
import com.kharchabook.util.ValidationUtil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/user/dashboard")
public class UserDashboardServlet extends HttpServlet {

    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final BudgetDAO budgetDAO = new BudgetDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final SavingsGoalDAO savingsGoalDAO = new SavingsGoalDAO();
    private final BillReminderDAO billReminderDAO = new BillReminderDAO();
    private final FeeDAO feeDAO = new FeeDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        int userId = (Integer) session.getAttribute(SessionKeys.USER_ID);
        LocalDate today = LocalDate.now();
        int y = today.getYear();
        int m = today.getMonthValue();
        try {
            BigDecimal income = transactionDAO.sumIncomeForMonth(userId, y, m);
            BigDecimal expense = transactionDAO.sumExpenseForMonth(userId, y, m);
            BigDecimal netBalance = income.subtract(expense);
            req.setAttribute("incomeMonth", income);
            req.setAttribute("expenseMonth", expense);
            req.setAttribute("netBalance", netBalance);

            BigDecimal todayExpense = transactionDAO.sumExpenseForDay(userId, today);
            int todayCategoryCount = transactionDAO.countExpenseCategoriesForDay(userId, today);
            req.setAttribute("todayExpense", todayExpense);
            req.setAttribute("todayCategoryCount", todayCategoryCount);

            BigDecimal reservedAmount = savingsGoalDAO.sumSavedAmountByStatus(userId, "active");
            req.setAttribute("reservedAmount", reservedAmount);
            req.setAttribute("availableAfterReserve", netBalance.subtract(reservedAmount));

            List<BillReminder> dueSoonBills = billReminderDAO.findDueSoon(userId, today, today.plusDays(7));
            req.setAttribute("dueSoonBills", dueSoonBills);
            if (!dueSoonBills.isEmpty()) {
                BillReminder firstDueSoonBill = dueSoonBills.get(0);
                String popupKey = "billReminderPopup:" + firstDueSoonBill.getId() + ":" + firstDueSoonBill.getNextDueDate() + ":" + dueSoonBills.size();
                req.setAttribute("billReminderPopupKey", popupKey);
            }

            // Fee reminder logic - check for fees due in next 10 days
            try {
                List<Fee> criticalFees = feeDAO.findCriticalFees(userId, today, 10);
                req.setAttribute("criticalFees", criticalFees);
                
                // Check if net balance is less than any critical fee
                List<Fee> urgentFees = new ArrayList<>();
                for (Fee fee : criticalFees) {
                    if (netBalance.compareTo(fee.getAmount()) < 0) {
                        urgentFees.add(fee);
                    }
                }
                req.setAttribute("urgentFees", urgentFees);
                if (!urgentFees.isEmpty()) {
                    Fee firstUrgentFee = urgentFees.get(0);
                    String popupKey = "feeWarningPopup:" + firstUrgentFee.getId() + ":" + firstUrgentFee.getDueDate() + ":" + urgentFees.size();
                    req.setAttribute("feeWarningPopupKey", popupKey);
                }
            } catch (SQLException e) {
                // Handle case where fees table doesn't exist yet
                req.setAttribute("criticalFees", new ArrayList<>());
                req.setAttribute("urgentFees", new ArrayList<>());
                // Log the error for debugging
                System.err.println("Fee reminder functionality not available: " + e.getMessage());
            }

            int topCatId = transactionDAO.topExpenseCategoryIdForMonth(userId, y, m);
            if (topCatId > 0) {
                req.setAttribute("topCategoryName", categoryDAO.findById(topCatId).getName());
            } else {
                req.setAttribute("topCategoryName", "-");
            }

            List<Budget> budgets = budgetDAO.findForUserMonth(userId, y, m, transactionDAO);
            List<String> warnings = new ArrayList<>();
            List<String> exceeded = new ArrayList<>();
            for (Budget b : budgets) {
                BigDecimal lim = b.getMonthlyLimit();
                BigDecimal sp = b.getSpentAmount() != null ? b.getSpentAmount() : BigDecimal.ZERO;
                if (lim != null && lim.compareTo(BigDecimal.ZERO) > 0) {
                    double pct = sp.multiply(BigDecimal.valueOf(100)).divide(lim, 2, java.math.RoundingMode.HALF_UP).doubleValue();
                    if (sp.compareTo(lim) > 0) {
                        exceeded.add(b.getCategoryName() + " (spent NPR " + sp + " / limit " + lim + ")");
                    } else if (pct >= 80) {
                        warnings.add(b.getCategoryName() + " (" + String.format("%.0f", pct) + "% of budget used)");
                    }
                }
            }
            req.setAttribute("budgetWarnings", warnings);
            req.setAttribute("budgetExceeded", exceeded);

            String purchasePriceRaw = req.getParameter("purchasePrice");
            req.setAttribute("purchasePrice", purchasePriceRaw);
            if (!ValidationUtil.isBlank(purchasePriceRaw)) {
                StringBuilder affordabilityError = new StringBuilder();
                BigDecimal purchasePrice = ValidationUtil.parseAmount(purchasePriceRaw, affordabilityError);
                if (purchasePrice != null) {
                    BigDecimal budgetRoom = BigDecimal.ZERO;
                    for (Budget budget : budgets) {
                        BigDecimal spent = budget.getSpentAmount() != null ? budget.getSpentAmount() : BigDecimal.ZERO;
                        BigDecimal remaining = budget.getMonthlyLimit().subtract(spent);
                        if (remaining.compareTo(BigDecimal.ZERO) > 0) {
                            budgetRoom = budgetRoom.add(remaining);
                        }
                    }
                    BigDecimal protectedBalance = netBalance.subtract(reservedAmount);
                    String affordabilityMessage;
                    String affordabilityStatus;
                    if (purchasePrice.compareTo(protectedBalance) <= 0 && purchasePrice.compareTo(budgetRoom) <= 0) {
                        affordabilityMessage = "Yes, you can afford this and still meet your goals.";
                        affordabilityStatus = "success";
                    } else if (purchasePrice.compareTo(protectedBalance) <= 0) {
                        affordabilityMessage = "You can afford this, but one or more budget categories will be tight.";
                        affordabilityStatus = "warn";
                    } else if (purchasePrice.compareTo(netBalance) <= 0) {
                        affordabilityMessage = "You can buy this, but it will reduce the money set aside for savings goals.";
                        affordabilityStatus = "warn";
                    } else {
                        affordabilityMessage = "No — this will break your monthly budget or savings plan.";
                        affordabilityStatus = "danger";
                    }
                    req.setAttribute("affordabilityMessage", affordabilityMessage);
                    req.setAttribute("affordabilityStatus", affordabilityStatus);
                } else {
                    req.setAttribute("affordabilityError", affordabilityError.toString());
                }
            }

            List<TransactionRecord> recent = transactionDAO.findRecent(userId, 5);
            req.setAttribute("recentTransactions", recent);

            List<SavingsGoal> activeGoals = savingsGoalDAO.findByUserAndStatus(userId, "active");
            req.setAttribute("activeGoalCount", activeGoals.size());
            req.setAttribute("dueSoonGoals", savingsGoalDAO.findDueSoonActiveGoals(userId, today, today.plusDays(14)));

            int prevMonth = m == 1 ? 12 : m - 1;
            int prevYear = m == 1 ? y - 1 : y;

            Map<Integer, BigDecimal> categoryMap = transactionDAO.expenseByCategoryForMonth(userId, y, m);
            List<Map<String, Object>> expenseBreakdown = new ArrayList<>();
            for (Map.Entry<Integer, BigDecimal> entry : categoryMap.entrySet()) {
                if (entry.getValue() == null || entry.getValue().compareTo(BigDecimal.ZERO) <= 0) {
                    continue;
                }
                Map<String, Object> row = new HashMap<>();
                row.put("name", categoryDAO.findById(entry.getKey()).getName());
                row.put("amount", entry.getValue());
                double percent = expense.compareTo(BigDecimal.ZERO) > 0
                        ? entry.getValue().multiply(BigDecimal.valueOf(100)).divide(expense, 2, java.math.RoundingMode.HALF_UP).doubleValue()
                        : 0;
                row.put("percent", percent);

                BigDecimal previousAmount = transactionDAO.sumExpenseForCategoryMonth(userId, entry.getKey(), prevYear, prevMonth);
                String trendState;
                String trendText;
                if (entry.getValue().compareTo(previousAmount) < 0) {
                    trendState = "down";
                    trendText = "🟢 Spending less than last month";
                } else if (entry.getValue().compareTo(previousAmount) > 0) {
                    trendState = "up";
                    trendText = "🔴 Spending more than last month";
                } else {
                    trendState = "same";
                    trendText = "➡️ Same as last month";
                }
                row.put("trendState", trendState);
                row.put("trendText", trendText);
                row.put("previousAmount", previousAmount);
                expenseBreakdown.add(row);
            }
            expenseBreakdown.sort(Comparator.comparing((Map<String, Object> row) -> (BigDecimal) row.get("amount")).reversed());
            if (expenseBreakdown.size() > 5) {
                expenseBreakdown = new ArrayList<>(expenseBreakdown.subList(0, 5));
            }
            req.setAttribute("expenseBreakdown", expenseBreakdown);

            BigDecimal prevExpense = transactionDAO.sumExpenseForMonth(userId, prevYear, prevMonth);
            req.setAttribute("prevExpenseMonth", prevExpense);

            String insight;
            if (expense.compareTo(BigDecimal.ZERO) == 0 && income.compareTo(BigDecimal.ZERO) == 0) {
                insight = "Start by adding a few transactions so your dashboard can show your real monthly pattern.";
            } else if (prevExpense.compareTo(BigDecimal.ZERO) > 0 && expense.compareTo(prevExpense) > 0) {
                insight = "Your expenses are higher than last month. Review your regular categories before the month ends.";
            } else if (reservedAmount.compareTo(BigDecimal.ZERO) > 0) {
                insight = "You already have money set aside in active goals. This helps protect that amount from daily spending.";
            } else {
                insight = "A small savings goal for school fees, rent, or emergencies can help you manage upcoming costs more safely.";
            }
            req.setAttribute("dashboardInsight", insight);

            req.getRequestDispatcher("/user/dashboard.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
