package com.kharchabook.servlet;

import com.kharchabook.dao.SavingsGoalDAO;
import com.kharchabook.model.SavingsGoal;
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
 * Handles savings goal management.
 * Supports creating, updating, completing, and tracking savings goals.
 */
@WebServlet("/user/goals")
public class SavingsGoalServlet extends HttpServlet {

    // DAO object for savings goal operations
    private final SavingsGoalDAO savingsGoalDAO = new SavingsGoalDAO();

    /**
     * Loads savings goal pages and goal list.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Get current session and logged-in user ID
        HttpSession session = req.getSession();
        int userId = (Integer) session.getAttribute(SessionKeys.USER_ID);

        // Get action from request
        String action = req.getParameter("action");

        try {
            // Open add goal form
            if ("new".equals(action)) {
                req.getRequestDispatcher("/user/addGoal.jsp").forward(req, resp);
                return;
            }

            // Open edit goal form
            if ("edit".equals(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                SavingsGoal g = savingsGoalDAO.findById(id, userId);

                if (g == null || !"active".equals(g.getStatus())) {
                    resp.sendRedirect(req.getContextPath() + "/user/goals");
                    return;
                }

                req.setAttribute("goal", g);
                req.getRequestDispatcher("/user/editGoal.jsp").forward(req, resp);
                return;
            }

            // Load active and completed goals
            List<SavingsGoal> active = savingsGoalDAO.findByUserAndStatus(userId, "active");
            List<SavingsGoal> completed = savingsGoalDAO.findByUserAndStatus(userId, "completed");

            req.setAttribute("activeGoals", active);
            req.setAttribute("completedGoals", completed);

            req.getRequestDispatcher("/user/savingsGoals.jsp").forward(req, resp);

        } catch (SQLException e) {
            throw new ServletException(e);
        }
    }

    /**
     * Handles savings goal actions such as create, update, add money, complete, and cancel.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Get current session and logged-in user ID
        HttpSession session = req.getSession();
        int userId = (Integer) session.getAttribute(SessionKeys.USER_ID);

        // Get action from request
        String action = req.getParameter("action");

        try {
            // Create new savings goal
            if ("create".equals(action)) {
                String err = create(req, userId);

                if (err != null) {
                    session.setAttribute(SessionKeys.FLASH_ERROR, err);
                } else {
                    session.setAttribute(SessionKeys.FLASH_SUCCESS, "Savings goal created.");
                }

                resp.sendRedirect(req.getContextPath() + "/user/goals");
                return;
            }

            // Add money to existing goal
            if ("addMoney".equals(action)) {
                String err = addMoney(req, userId);

                if (err != null) {
                    session.setAttribute(SessionKeys.FLASH_ERROR, err);
                } else {
                    session.setAttribute(SessionKeys.FLASH_SUCCESS, "Amount added to goal.");
                }

                resp.sendRedirect(req.getContextPath() + "/user/goals");
                return;
            }

            // Mark goal as completed
            if ("complete".equals(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                savingsGoalDAO.setStatus(id, userId, "completed");

                session.setAttribute(SessionKeys.FLASH_SUCCESS, "Goal marked complete.");
                resp.sendRedirect(req.getContextPath() + "/user/goals");
                return;
            }

            // Cancel goal
            if ("cancel".equals(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                savingsGoalDAO.setStatus(id, userId, "cancelled");

                session.setAttribute(SessionKeys.FLASH_SUCCESS, "Goal cancelled.");
                resp.sendRedirect(req.getContextPath() + "/user/goals");
                return;
            }

            // Update goal
            if ("update".equals(action)) {
                String err = update(req, userId);

                if (err != null) {
                    session.setAttribute(SessionKeys.FLASH_ERROR, err);
                } else {
                    session.setAttribute(SessionKeys.FLASH_SUCCESS, "Goal updated.");
                }

                resp.sendRedirect(req.getContextPath() + "/user/goals");
                return;
            }

            resp.sendRedirect(req.getContextPath() + "/user/goals");

        } catch (SQLException e) {
            throw new IOException(e);
        }
    }

    /**
     * Validates and creates a new savings goal.
     */
    private String create(HttpServletRequest req, int userId) throws SQLException {
        String title = req.getParameter("title");
        String targetRaw = req.getParameter("targetAmount");
        String deadlineRaw = req.getParameter("deadline");

        // Check required fields
        if (ValidationUtil.isBlank(title) || ValidationUtil.isBlank(targetRaw)) {
            return "This field is required. Please fill in all required fields before submitting.";
        }

        // Validate target amount
        BigDecimal target;
        try {
            target = new BigDecimal(targetRaw.trim());

            if (target.compareTo(BigDecimal.ZERO) <= 0) {
                return "Target amount must be greater than zero to create a savings goal.";
            }
        } catch (NumberFormatException e) {
            return "Target amount must be greater than zero to create a savings goal.";
        }

        // Create savings goal object
        SavingsGoal g = new SavingsGoal();
        g.setUserId(userId);
        g.setTitle(title.trim());
        g.setTargetAmount(target.setScale(2, java.math.RoundingMode.HALF_UP));
        g.setSavedAmount(BigDecimal.ZERO);

        // Validate optional deadline
        if (!ValidationUtil.isBlank(deadlineRaw)) {
            try {
                g.setDeadline(LocalDate.parse(deadlineRaw.trim()));
            } catch (DateTimeParseException e) {
                return "Invalid deadline date.";
            }
        }

        g.setStatus("active");

        // Save goal
        savingsGoalDAO.insert(g);
        return null;
    }

    /**
     * Validates and adds money to savings goal.
     */
    private String addMoney(HttpServletRequest req, int userId) throws SQLException {
        StringBuilder err = new StringBuilder();

        int id = Integer.parseInt(req.getParameter("id"));
        BigDecimal add = ValidationUtil.parseAmount(req.getParameter("amount"), err);

        // Validate amount
        if (add == null) {
            return err.toString();
        }

        // Find goal
        SavingsGoal g = savingsGoalDAO.findById(id, userId);

        if (g == null || !"active".equals(g.getStatus())) {
            return "Goal not found.";
        }

        // Check if amount exceeds remaining target
        BigDecimal remaining = g.getTargetAmount().subtract(
                g.getSavedAmount() != null ? g.getSavedAmount() : BigDecimal.ZERO
        );

        if (add.compareTo(remaining) > 0) {
            return "Amount added exceeds your remaining goal target. Please enter a smaller amount.";
        }

        // Add amount
        savingsGoalDAO.addSavedAmount(id, userId, add);
        return null;
    }

    /**
     * Validates and updates an existing savings goal.
     */
    private String update(HttpServletRequest req, int userId) throws SQLException {
        int id = Integer.parseInt(req.getParameter("id"));
        String title = req.getParameter("title");
        String targetRaw = req.getParameter("targetAmount");
        String deadlineRaw = req.getParameter("deadline");

        // Check required fields
        if (ValidationUtil.isBlank(title) || ValidationUtil.isBlank(targetRaw)) {
            return "This field is required. Please fill in all required fields before submitting.";
        }

        // Validate target amount
        BigDecimal target;
        try {
            target = new BigDecimal(targetRaw.trim());

            if (target.compareTo(BigDecimal.ZERO) <= 0) {
                return "Target amount must be greater than zero to create a savings goal.";
            }
        } catch (NumberFormatException e) {
            return "Invalid target amount.";
        }

        // Validate deadline
        LocalDate deadline = null;
        if (!ValidationUtil.isBlank(deadlineRaw)) {
            try {
                deadline = LocalDate.parse(deadlineRaw.trim());
            } catch (DateTimeParseException e) {
                return "Invalid deadline.";
            }
        }

        // Update goal
        savingsGoalDAO.updateGoal(
                id,
                userId,
                title.trim(),
                target.setScale(2, java.math.RoundingMode.HALF_UP),
                deadline
        );

        return null;
    }
}