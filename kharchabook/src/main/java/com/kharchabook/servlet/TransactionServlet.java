package com.kharchabook.servlet;

import com.kharchabook.dao.CategoryDAO;
import com.kharchabook.dao.TransactionDAO;
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
import java.time.format.DateTimeParseException;
import java.util.List;

/**
 * Handles transaction management.
 * Supports viewing, adding, editing, updating, and deleting transactions.
 */
@WebServlet("/user/transactions")
public class TransactionServlet extends HttpServlet {

    // DAO objects for transaction and category operations
    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();

    /**
     * Loads transaction pages and transaction list.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Get current session and logged-in user ID
        HttpSession session = req.getSession();
        int userId = (Integer) session.getAttribute(SessionKeys.USER_ID);

        // Get action from request
        String action = req.getParameter("action");

        try {
            // Open add transaction form
            if ("add".equals(action)) {
                req.setAttribute("incomeCategories", categoryDAO.findByType("income"));
                req.setAttribute("expenseCategories", categoryDAO.findByType("expense"));
                req.getRequestDispatcher("/user/addTransaction.jsp").forward(req, resp);
                return;
            }

            // Open edit transaction form
            if ("edit".equals(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                TransactionRecord t = transactionDAO.findById(id, userId);

                if (t == null) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }

                req.setAttribute("tx", t);
                req.setAttribute("incomeCategories", categoryDAO.findByType("income"));
                req.setAttribute("expenseCategories", categoryDAO.findByType("expense"));
                req.getRequestDispatcher("/user/editTransaction.jsp").forward(req, resp);
                return;
            }

            // Read filter values
            Integer year = parseIntOrNull(req.getParameter("year"));
            Integer month = parseIntOrNull(req.getParameter("month"));
            String type = req.getParameter("type");
            Integer catId = parseIntOrNull(req.getParameter("categoryId"));

            // Default year to current year
            if (year == null) {
                year = LocalDate.now().getYear();
            }

            // Load filtered transaction list
            List<TransactionRecord> list = transactionDAO.findForUser(
                    userId,
                    year,
                    month,
                    type == null || type.isEmpty() ? null : type,
                    catId == null || catId <= 0 ? null : catId
            );

            // Send data to JSP
            req.setAttribute("transactions", list);
            req.setAttribute("filterYear", year);
            req.setAttribute("filterMonth", month);
            req.setAttribute("filterType", type);
            req.setAttribute("filterCategoryId", catId);
            req.setAttribute("incomeCategories", categoryDAO.findByType("income"));
            req.setAttribute("expenseCategories", categoryDAO.findByType("expense"));

            req.getRequestDispatcher("/user/viewTransactions.jsp").forward(req, resp);

        } catch (SQLException e) {
            throw new ServletException(e);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/user/transactions");
        }
    }

    /**
     * Handles transaction add, update, and delete actions.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Get current session and logged-in user ID
        HttpSession session = req.getSession();
        int userId = (Integer) session.getAttribute(SessionKeys.USER_ID);

        // Get form action
        String action = req.getParameter("action");

        try {
            // Add new transaction
            if ("add".equals(action)) {
                String err = validateAndAdd(req, userId);

                if (err != null) {
                    session.setAttribute(SessionKeys.FLASH_ERROR, err);
                } else {
                    session.setAttribute(SessionKeys.FLASH_SUCCESS, "Transaction added.");
                }

                resp.sendRedirect(req.getContextPath() + "/user/transactions");
                return;
            }

            // Update existing transaction
            if ("update".equals(action)) {
                String err = validateAndUpdate(req, userId);

                if (err != null) {
                    session.setAttribute(SessionKeys.FLASH_ERROR, err);
                } else {
                    session.setAttribute(SessionKeys.FLASH_SUCCESS, "Transaction updated.");
                }

                resp.sendRedirect(req.getContextPath() + "/user/transactions");
                return;
            }

            // Delete transaction
            if ("delete".equals(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                transactionDAO.delete(id, userId);

                session.setAttribute(SessionKeys.FLASH_SUCCESS, "Transaction deleted.");
                resp.sendRedirect(req.getContextPath() + "/user/transactions");
                return;
            }

        } catch (SQLException e) {
            throw new IOException(e);
        }
    }

    /**
     * Validates and inserts new transaction.
     */
    private String validateAndAdd(HttpServletRequest req, int userId) throws SQLException {
        StringBuilder err = new StringBuilder();

        // Read form values
        String type = req.getParameter("type");
        String amountRaw = req.getParameter("amount");
        String categoryIdRaw = req.getParameter("categoryId");
        String dateRaw = req.getParameter("transactionDate");
        String description = req.getParameter("description");

        // Check required fields
        if (ValidationUtil.isBlank(type) || ValidationUtil.isBlank(categoryIdRaw) || ValidationUtil.isBlank(dateRaw)) {
            return "This field is required. Please fill in all required fields before submitting.";
        }

        // Validate amount
        BigDecimal amount = ValidationUtil.parseAmount(amountRaw, err);
        if (amount == null) {
            return err.toString();
        }

        // Validate category
        int categoryId = Integer.parseInt(categoryIdRaw.trim());
        var cat = categoryDAO.findById(categoryId);

        if (cat == null || !cat.getType().equals(type)) {
            return "Invalid category for selected type.";
        }

        // Validate date
        LocalDate d;
        try {
            d = LocalDate.parse(dateRaw.trim());
        } catch (DateTimeParseException e) {
            return "Invalid date.";
        }

        // Create transaction object
        TransactionRecord t = new TransactionRecord();
        t.setUserId(userId);
        t.setCategoryId(categoryId);
        t.setType(type);
        t.setAmount(amount);
        t.setDescription(description != null ? description.trim() : null);
        t.setTransactionDate(d);

        // Save transaction
        transactionDAO.insert(t);
        return null;
    }

    /**
     * Validates and updates existing transaction.
     */
    private String validateAndUpdate(HttpServletRequest req, int userId) throws SQLException {
        StringBuilder err = new StringBuilder();

        // Read form values
        int id = Integer.parseInt(req.getParameter("id"));
        String type = req.getParameter("type");
        String amountRaw = req.getParameter("amount");
        String categoryIdRaw = req.getParameter("categoryId");
        String dateRaw = req.getParameter("transactionDate");
        String description = req.getParameter("description");

        // Check required fields
        if (ValidationUtil.isBlank(type) || ValidationUtil.isBlank(categoryIdRaw) || ValidationUtil.isBlank(dateRaw)) {
            return "This field is required. Please fill in all required fields before submitting.";
        }

        // Validate amount
        BigDecimal amount = ValidationUtil.parseAmount(amountRaw, err);
        if (amount == null) {
            return err.toString();
        }

        // Validate category
        int categoryId = Integer.parseInt(categoryIdRaw.trim());
        var cat = categoryDAO.findById(categoryId);

        if (cat == null || !cat.getType().equals(type)) {
            return "Invalid category for selected type.";
        }

        // Validate date
        LocalDate d;
        try {
            d = LocalDate.parse(dateRaw.trim());
        } catch (DateTimeParseException e) {
            return "Invalid date.";
        }

        // Create updated transaction object
        TransactionRecord t = new TransactionRecord();
        t.setId(id);
        t.setUserId(userId);
        t.setCategoryId(categoryId);
        t.setType(type);
        t.setAmount(amount);
        t.setDescription(description != null ? description.trim() : null);
        t.setTransactionDate(d);

        // Update transaction
        transactionDAO.update(t);
        return null;
    }

    /**
     * Converts string to integer safely.
     * Returns null if value is invalid.
     */
    private static Integer parseIntOrNull(String s) {
        if (s == null || s.trim().isEmpty()) {
            return null;
        }

        try {
            return Integer.parseInt(s.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}