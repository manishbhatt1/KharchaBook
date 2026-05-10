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

@WebServlet("/user/transactions")
public class TransactionServlet extends HttpServlet {

    private final TransactionDAO transactionDAO = new TransactionDAO();
    private final CategoryDAO categoryDAO = new CategoryDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        int userId = (Integer) session.getAttribute(SessionKeys.USER_ID);
        String action = req.getParameter("action");

        try {
            if ("add".equals(action)) {
                req.setAttribute("incomeCategories", categoryDAO.findByType("income"));
                req.setAttribute("expenseCategories", categoryDAO.findByType("expense"));
                req.getRequestDispatcher("/user/addTransaction.jsp").forward(req, resp);
                return;
            }

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

            Integer year = parseIntOrNull(req.getParameter("year"));
            Integer month = parseIntOrNull(req.getParameter("month"));
            String type = req.getParameter("type");
            Integer catId = parseIntOrNull(req.getParameter("categoryId"));

            if (year == null) {
                year = LocalDate.now().getYear();
            }

            List<TransactionRecord> list = transactionDAO.findForUser(
                    userId,
                    year,
                    month,
                    type == null || type.isEmpty() ? null : type,
                    catId == null || catId <= 0 ? null : catId
            );

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

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        int userId = (Integer) session.getAttribute(SessionKeys.USER_ID);
        String action = req.getParameter("action");

        try {
            if ("add".equals(action)) {
                String err = validateAndAdd(req, userId);
                session.setAttribute(err == null ? SessionKeys.FLASH_SUCCESS : SessionKeys.FLASH_ERROR,
                        err == null ? "Transaction added." : err);
                resp.sendRedirect(req.getContextPath() + "/user/transactions");
                return;
            }

            if ("update".equals(action)) {
                String err = validateAndUpdate(req, userId);
                session.setAttribute(err == null ? SessionKeys.FLASH_SUCCESS : SessionKeys.FLASH_ERROR,
                        err == null ? "Transaction updated." : err);
                resp.sendRedirect(req.getContextPath() + "/user/transactions");
                return;
            }

            if ("delete".equals(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                transactionDAO.delete(id, userId);
                session.setAttribute(SessionKeys.FLASH_SUCCESS, "Transaction deleted.");
                resp.sendRedirect(req.getContextPath() + "/user/transactions");
            }
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }

    private String validateAndAdd(HttpServletRequest req, int userId) throws SQLException {
        StringBuilder err = new StringBuilder();
        String type = req.getParameter("type");
        String amountRaw = req.getParameter("amount");
        String categoryIdRaw = req.getParameter("categoryId");
        String dateRaw = req.getParameter("transactionDate");
        String description = req.getParameter("description");

        if (ValidationUtil.isBlank(type) || ValidationUtil.isBlank(categoryIdRaw) || ValidationUtil.isBlank(dateRaw)) {
            return "This field is required. Please fill in all required fields before submitting.";
        }

        BigDecimal amount = ValidationUtil.parseAmount(amountRaw, err);
        if (amount == null) {
            return err.toString();
        }

        int categoryId = Integer.parseInt(categoryIdRaw.trim());
        var cat = categoryDAO.findById(categoryId);
        if (cat == null || !cat.getType().equals(type)) {
            return "Invalid category for selected type.";
        }

        LocalDate d;
        try {
            d = LocalDate.parse(dateRaw.trim());
        } catch (DateTimeParseException e) {
            return "Invalid date.";
        }

        TransactionRecord t = new TransactionRecord();
        t.setUserId(userId);
        t.setCategoryId(categoryId);
        t.setType(type);
        t.setAmount(amount);
        t.setDescription(description != null ? description.trim() : null);
        t.setTransactionDate(d);
        transactionDAO.insert(t);
        return null;
    }

    private String validateAndUpdate(HttpServletRequest req, int userId) throws SQLException {
        StringBuilder err = new StringBuilder();
        int id = Integer.parseInt(req.getParameter("id"));
        String type = req.getParameter("type");
        String amountRaw = req.getParameter("amount");
        String categoryIdRaw = req.getParameter("categoryId");
        String dateRaw = req.getParameter("transactionDate");
        String description = req.getParameter("description");

        if (ValidationUtil.isBlank(type) || ValidationUtil.isBlank(categoryIdRaw) || ValidationUtil.isBlank(dateRaw)) {
            return "This field is required. Please fill in all required fields before submitting.";
        }

        BigDecimal amount = ValidationUtil.parseAmount(amountRaw, err);
        if (amount == null) {
            return err.toString();
        }

        int categoryId = Integer.parseInt(categoryIdRaw.trim());
        var cat = categoryDAO.findById(categoryId);
        if (cat == null || !cat.getType().equals(type)) {
            return "Invalid category for selected type.";
        }

        LocalDate d;
        try {
            d = LocalDate.parse(dateRaw.trim());
        } catch (DateTimeParseException e) {
            return "Invalid date.";
        }

        TransactionRecord t = new TransactionRecord();
        t.setId(id);
        t.setUserId(userId);
        t.setCategoryId(categoryId);
        t.setType(type);
        t.setAmount(amount);
        t.setDescription(description != null ? description.trim() : null);
        t.setTransactionDate(d);
        transactionDAO.update(t);
        return null;
    }

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
