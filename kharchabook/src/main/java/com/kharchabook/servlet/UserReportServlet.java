package com.kharchabook.servlet;

import com.kharchabook.dao.CategoryDAO;
import com.kharchabook.dao.TransactionDAO;
import com.kharchabook.model.Category;
import com.kharchabook.util.SessionKeys;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/user/reports")
public class UserReportServlet extends HttpServlet {

    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        int userId = (Integer) session.getAttribute(SessionKeys.USER_ID);
        int year = LocalDate.now().getYear();
        String yp = req.getParameter("year");
        if (yp != null && !yp.isEmpty()) {
            year = Integer.parseInt(yp.trim());
        }
        try {
            List<Object[]> monthlyRows = new ArrayList<>();
            for (int m = 1; m <= 12; m++) {
                BigDecimal inc = transactionDAO.totalIncomeYearMonth(userId, year, m);
                BigDecimal exp = transactionDAO.totalExpenseYearMonth(userId, year, m);
                monthlyRows.add(new Object[]{m, inc, exp, inc.subtract(exp)});
            }
            req.setAttribute("monthlyRows", monthlyRows);
            req.setAttribute("reportYear", year);

            LocalDate now = LocalDate.now();
            Map<Integer, BigDecimal> expByCat = transactionDAO.expenseByCategoryForMonth(userId, now.getYear(), now.getMonthValue());
            BigDecimal totalExp = transactionDAO.sumExpenseForMonth(userId, now.getYear(), now.getMonthValue());
            List<Map<String, Object>> catBreakdown = new ArrayList<>();
            for (Map.Entry<Integer, BigDecimal> e : expByCat.entrySet()) {
                Category c = categoryDAO.findById(e.getKey());
                if (c == null) {
                    continue;
                }
                BigDecimal amt = e.getValue();
                double pct = totalExp.compareTo(BigDecimal.ZERO) > 0
                        ? amt.multiply(BigDecimal.valueOf(100)).divide(totalExp, 1, RoundingMode.HALF_UP).doubleValue()
                        : 0;
                Map<String, Object> row = new HashMap<>();
                row.put("name", c.getName());
                row.put("amount", amt);
                row.put("percent", pct);
                catBreakdown.add(row);
            }
            req.setAttribute("categoryBreakdown", catBreakdown);
            req.setAttribute("breakdownMonth", now.getMonthValue());
            req.setAttribute("breakdownYear", now.getYear());

            List<Object[]> last6 = new ArrayList<>();
            for (int i = 5; i >= 0; i--) {
                LocalDate d = now.minusMonths(i);
                BigDecimal inc = transactionDAO.totalIncomeYearMonth(userId, d.getYear(), d.getMonthValue());
                BigDecimal exp = transactionDAO.totalExpenseYearMonth(userId, d.getYear(), d.getMonthValue());
                last6.add(new Object[]{d.getYear(), d.getMonthValue(), inc, exp});
            }
            req.setAttribute("last6Months", last6);

            int maxMonth = 1;
            BigDecimal maxExp = BigDecimal.ZERO;
            for (int m = 1; m <= 12; m++) {
                BigDecimal exp = transactionDAO.totalExpenseYearMonth(userId, year, m);
                if (exp.compareTo(maxExp) > 0) {
                    maxExp = exp;
                    maxMonth = m;
                }
            }
            req.setAttribute("highestExpenseMonth", maxMonth);
            req.setAttribute("highestExpenseAmount", maxExp);

            req.getRequestDispatcher("/user/reports.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }
}
