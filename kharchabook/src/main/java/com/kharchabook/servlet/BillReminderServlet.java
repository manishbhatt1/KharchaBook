package com.kharchabook.servlet;

import com.kharchabook.dao.BillReminderDAO;
import com.kharchabook.model.BillReminder;
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
import java.util.List;

@WebServlet("/user/bills")
public class BillReminderServlet extends HttpServlet {

    private final BillReminderDAO billReminderDAO = new BillReminderDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession();
        int userId = (Integer) session.getAttribute(SessionKeys.USER_ID);
        try {
            String action = req.getParameter("action");
            if ("edit".equals(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                BillReminder editReminder = billReminderDAO.findById(id, userId);
                req.setAttribute("editReminder", editReminder);
            }
            LocalDate today = LocalDate.now();
            List<BillReminder> reminders = billReminderDAO.findByUser(userId);
            req.setAttribute("billReminders", reminders);
            req.setAttribute("dueSoonBills", billReminderDAO.findDueSoon(userId, today, today.plusDays(7)));
            req.getRequestDispatcher("/user/billReminders.jsp").forward(req, resp);
        } catch (SQLException e) {
            throw new ServletException(e);
        } catch (NumberFormatException e) {
            resp.sendRedirect(req.getContextPath() + "/user/bills");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        int userId = (Integer) session.getAttribute(SessionKeys.USER_ID);
        String action = req.getParameter("action");
        try {
            if ("create".equals(action)) {
                String err = saveReminder(req, userId, false);
                session.setAttribute(err == null ? SessionKeys.FLASH_SUCCESS : SessionKeys.FLASH_ERROR,
                        err == null ? "Bill reminder saved." : err);
                resp.sendRedirect(req.getContextPath() + "/user/bills");
                return;
            }
            if ("update".equals(action)) {
                String err = saveReminder(req, userId, true);
                session.setAttribute(err == null ? SessionKeys.FLASH_SUCCESS : SessionKeys.FLASH_ERROR,
                        err == null ? "Bill reminder updated." : err);
                String id = req.getParameter("id");
                resp.sendRedirect(req.getContextPath() + "/user/bills" + (id != null ? "?action=edit&id=" + id : ""));
                return;
            }
            if ("delete".equals(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                billReminderDAO.delete(id, userId);
                session.setAttribute(SessionKeys.FLASH_SUCCESS, "Bill reminder removed.");
                resp.sendRedirect(req.getContextPath() + "/user/bills");
                return;
            }
            if ("toggle".equals(action)) {
                int id = Integer.parseInt(req.getParameter("id"));
                String status = req.getParameter("status");
                String nextStatus = "active".equalsIgnoreCase(status) ? "paused" : "active";
                billReminderDAO.updateStatus(id, userId, nextStatus);
                session.setAttribute(SessionKeys.FLASH_SUCCESS, "Bill reminder status updated.");
                resp.sendRedirect(req.getContextPath() + "/user/bills");
                return;
            }
        } catch (SQLException e) {
            throw new IOException(e);
        }
    }

    private String saveReminder(HttpServletRequest req, int userId, boolean isUpdate) throws SQLException {
        String idRaw = req.getParameter("id");
        String billName = req.getParameter("billName");
        String amountRaw = req.getParameter("amount");
        String dueDateRaw = req.getParameter("dueDate");
        String frequency = req.getParameter("frequency");
        String notes = req.getParameter("notes");
        String status = req.getParameter("status");

        if (ValidationUtil.isBlank(billName) || ValidationUtil.isBlank(amountRaw) || ValidationUtil.isBlank(dueDateRaw) || ValidationUtil.isBlank(frequency)) {
            return "This field is required. Please fill in all required fields before submitting.";
        }

        BigDecimal amount = ValidationUtil.parseAmount(amountRaw, new StringBuilder());
        if (amount == null) {
            return "Please enter a valid numeric amount (e.g. 1500 or 1500.50).";
        }

        LocalDate dueDate;
        try {
            dueDate = LocalDate.parse(dueDateRaw.trim());
        } catch (Exception e) {
            return "Please choose a valid due date.";
        }

        if (!"monthly".equalsIgnoreCase(frequency) && !"yearly".equalsIgnoreCase(frequency)) {
            return "Please select a valid reminder frequency.";
        }

        BillReminder reminder = new BillReminder();
        if (isUpdate) {
            reminder.setId(Integer.parseInt(idRaw));
        }
        reminder.setUserId(userId);
        reminder.setBillName(billName.trim());
        reminder.setAmount(amount);
        reminder.setDueDate(dueDate);
        reminder.setFrequency(frequency.toLowerCase());
        reminder.setNotes(ValidationUtil.isBlank(notes) ? null : notes.trim());
        reminder.setStatus(ValidationUtil.isBlank(status) ? "active" : status.toLowerCase());

        if (isUpdate) {
            billReminderDAO.update(reminder);
        } else {
            billReminderDAO.insert(reminder);
        }
        return null;
    }
}
