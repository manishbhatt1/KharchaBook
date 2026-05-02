package com.kharchabook.servlet;

import com.kharchabook.dao.BudgetDAO;
import com.kharchabook.dao.CategoryDAO;
import com.kharchabook.dao.TransactionDAO;
import com.kharchabook.model.Budget;
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
import java.time.YearMonth;
import java.util.List;

@WebServlet("/user/budgets")
public class BudgetServlet extends HttpServlet {

    private final BudgetDAO budgetDAO = new BudgetDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();
    private final TransactionDAO transactionDAO = new TransactionDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        int userId = (Integer) session.getAttribute(SessionKeys.USER_ID);
        LocalDate today = LocalDate.now();
        int y = today.getYear();
        int m = today.getMonthValue();
        String budgetMonth = req.getParameter("budgetMonth");
        if (!ValidationUtil.isBlank(budgetMonth)) {
            try {
                YearMonth selectedMonth = YearMonth.parse(budgetMonth.trim());
                y = selectedMonth.getYear();
                m = selectedMonth.getMonthValue();
            } catch (Exception ignored) {
                budgetMonth = null;
            }
        }
        try {
            List<Budget> budgets = budgetDAO.findForUserMonth(userId, y, m, transactionDAO);
            req.setAttribute("budgets", budgets);
            req.setAttribute("expenseCategories", categoryDAO.findByType("expense"));
            req.setAttribute("year", y);
            req.setAttribute("month", m);
            req.setAttribute("budgetMonth", YearMonth.of(y, m).toString());
            String action = req.getParameter("action");
            if ("edit".equals(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                Budget b = budgetDAO.findById(id, userId);
                if (b != null) {
                    BigDecimal sp = transactionDAO.sumExpenseForCategoryMonth(userId, b.getCategoryId(), b.getYear(), b.getMonth());
                    b.setSpentAmount(sp);
                    req.setAttribute("editBudget", b);
                }
            }
            req.getRequestDispatcher("/user/setBudget.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        int userId = (Integer) session.getAttribute(SessionKeys.USER_ID);
        String action = req.getParameter("action");
        try {
            if ("create".equals(action)) {
                String err = createBudget(req, userId);
                if (err != null) {
                    session.setAttribute(SessionKeys.FLASH_ERROR, err);
                } else {
                    session.setAttribute(SessionKeys.FLASH_SUCCESS, "Budget saved.");
                }
            } else if ("update".equals(action)) {
                String err = updateBudget(req, userId);
                if (err != null) {
                    session.setAttribute(SessionKeys.FLASH_ERROR, err);
                } else {
                    session.setAttribute(SessionKeys.FLASH_SUCCESS, "Budget updated.");
                }
            } else if ("delete".equals(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                budgetDAO.delete(id, userId);
                session.setAttribute(SessionKeys.FLASH_SUCCESS, "Budget removed.");
            }
            String budgetMonth = req.getParameter("budgetMonth");
            String q = "";
            if (!ValidationUtil.isBlank(budgetMonth)) {
                q = "?budgetMonth=" + budgetMonth.trim();
            }
            resp.sendRedirect(req.getContextPath() + "/user/budgets" + q);
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }

    private String createBudget(HttpServletRequest req, int userId) throws SQLException {
        String categoryIdRaw = req.getParameter("categoryId");
        String limitRaw = req.getParameter("monthlyLimit");
        String budgetMonthRaw = req.getParameter("budgetMonth");
        if (ValidationUtil.isBlank(categoryIdRaw) || ValidationUtil.isBlank(limitRaw) || ValidationUtil.isBlank(budgetMonthRaw)) {
            return "This field is required. Please fill in all required fields before submitting.";
        }
        BigDecimal limit;
        try {
            limit = new BigDecimal(limitRaw.trim());
            if (limit.compareTo(BigDecimal.ZERO) <= 0) {
                return "Budget limit must be a positive number.";
            }
        } catch (NumberFormatException e) {
            return "Budget limit must be a positive number.";
        }
        int categoryId = Integer.parseInt(categoryIdRaw.trim());
        YearMonth budgetMonth;
        try {
            budgetMonth = YearMonth.parse(budgetMonthRaw.trim());
        } catch (Exception e) {
            return "Please choose a valid budget month.";
        }
        var cat = categoryDAO.findById(categoryId);
        if (cat == null || !"expense".equals(cat.getType())) {
            return "Select an expense category.";
        }
        Budget b = new Budget();
        b.setUserId(userId);
        b.setCategoryId(categoryId);
        b.setMonthlyLimit(limit.setScale(2, java.math.RoundingMode.HALF_UP));
        b.setMonth(budgetMonth.getMonthValue());
        b.setYear(budgetMonth.getYear());
        budgetDAO.insert(b);
        return null;
    }

    private String updateBudget(HttpServletRequest req, int userId) throws SQLException {
        int id = Integer.parseInt(req.getParameter("id"));
        String limitRaw = req.getParameter("monthlyLimit");
        if (ValidationUtil.isBlank(limitRaw)) {
            return "This field is required. Please fill in all required fields before submitting.";
        }
        BigDecimal limit;
        try {
            limit = new BigDecimal(limitRaw.trim());
            if (limit.compareTo(BigDecimal.ZERO) <= 0) {
                return "Budget limit must be a positive number.";
            }
        } catch (NumberFormatException e) {
            return "Budget limit must be a positive number.";
        }
        budgetDAO.update(id, userId, limit.setScale(2, java.math.RoundingMode.HALF_UP));
        return null;
    }
}
