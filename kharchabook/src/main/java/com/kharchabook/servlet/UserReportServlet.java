package com.kharchabook.servlet;

import com.kharchabook.dao.CategoryDAO;
import com.kharchabook.dao.TransactionDAO;
import com.kharchabook.model.Category;
import com.kharchabook.util.SessionKeys;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

/**
 * Servlet for generating user financial reports
 * URL: /user/reports
 */
@WebServlet("/user/reports")
public class UserReportServlet extends HttpServlet {

    // DAO objects for database operations
    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();

    /**
     * Handles GET request
     * Generates:
     * - Monthly income/expense report
     * - Category breakdown for current month
     * - Last 6 months summary
     * - Highest expense month
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Get session and logged-in user ID
        HttpSession session = req.getSession();
        int userId = (Integer) session.getAttribute(SessionKeys.USER_ID);

        // Default year = current year
        int year = LocalDate.now().getYear();

        // If user selects a different year from UI
        String yp = req.getParameter("year");
        if (yp != null && !yp.isEmpty()) {
            year = Integer.parseInt(yp.trim());
        }

        try {

            // =========================
            // MONTHLY REPORT (12 months)
            // =========================
            List<Object[]> monthlyRows = new ArrayList<>();

            for (int m = 1; m <= 12; m++) {
                // Total income and expense per month
                BigDecimal inc = transactionDAO.totalIncomeYearMonth(userId, year, m);
                BigDecimal exp = transactionDAO.totalExpenseYearMonth(userId, year, m);

                // Net = income - expense
                monthlyRows.add(new Object[]{m, inc, exp, inc.subtract(exp)});
            }

            // Send data to JSP
            req.setAttribute("monthlyRows", monthlyRows);
            req.setAttribute("reportYear", year);


            // =========================
            // CATEGORY BREAKDOWN (CURRENT MONTH)
            // =========================
            LocalDate now = LocalDate.now();

            // Expense grouped by category
            Map<Integer, BigDecimal> expByCat =
                    transactionDAO.expenseByCategoryForMonth(userId, now.getYear(), now.getMonthValue());

            // Total expense for percentage calculation
            BigDecimal totalExp =
                    transactionDAO.sumExpenseForMonth(userId, now.getYear(), now.getMonthValue());

            List<Map<String, Object>> catBreakdown = new ArrayList<>();

            for (Map.Entry<Integer, BigDecimal> e : expByCat.entrySet()) {

                // Get category details
                Category c = categoryDAO.findById(e.getKey());
                if (c == null) continue;

                BigDecimal amt = e.getValue();

                // Calculate percentage of total expense
                double pct = totalExp.compareTo(BigDecimal.ZERO) > 0
                        ? amt.multiply(BigDecimal.valueOf(100))
                             .divide(totalExp, 1, RoundingMode.HALF_UP)
                             .doubleValue()
                        : 0;

                // Store data for JSP
                Map<String, Object> row = new HashMap<>();
                row.put("name", c.getName());
                row.put("amount", amt);
                row.put("percent", pct);

                catBreakdown.add(row);
            }

            req.setAttribute("categoryBreakdown", catBreakdown);
            req.setAttribute("breakdownMonth", now.getMonthValue());
            req.setAttribute("breakdownYear", now.getYear());


            // =========================
            // LAST 6 MONTHS REPORT
            // =========================
            List<Object[]> last6 = new ArrayList<>();

            for (int i = 5; i >= 0; i--) {
                LocalDate d = now.minusMonths(i);

                BigDecimal inc = transactionDAO.totalIncomeYearMonth(userId, d.getYear(), d.getMonthValue());
                BigDecimal exp = transactionDAO.totalExpenseYearMonth(userId, d.getYear(), d.getMonthValue());

                last6.add(new Object[]{d.getYear(), d.getMonthValue(), inc, exp});
            }

            req.setAttribute("last6Months", last6);


            // =========================
            // HIGHEST EXPENSE MONTH
            // =========================
            int maxMonth = 1;
            BigDecimal maxExp = BigDecimal.ZERO;

            for (int m = 1; m <= 12; m++) {
                BigDecimal exp = transactionDAO.totalExpenseYearMonth(userId, year, m);

                // Find month with highest expense
                if (exp.compareTo(maxExp) > 0) {
                    maxExp = exp;
                    maxMonth = m;
                }
            }

            req.setAttribute("highestExpenseMonth", maxMonth);
            req.setAttribute("highestExpenseAmount", maxExp);


            // =========================
            // FORWARD TO JSP VIEW
            // =========================
            req.getRequestDispatcher("/user/reports.jsp").forward(req, resp);

        } catch (SQLException e) {
            // Handle database errors
            throw new ServletException(e);
        }
    }
}