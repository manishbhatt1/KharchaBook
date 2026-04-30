package com.kharchabook.dao;

import com.kharchabook.model.BillReminder;
import com.kharchabook.util.DBUtil;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class BillReminderDAO {

    public List<BillReminder> findByUser(int userId) throws SQLException {
        List<BillReminder> reminders = new ArrayList<>();
        String sql = "SELECT * FROM bill_reminders WHERE user_id = ? ORDER BY status, due_date, due_day, id";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    BillReminder reminder = map(rs);
                    decorateDates(reminder, LocalDate.now());
                    reminders.add(reminder);
                }
            }
        }
        return reminders;
    }

    public List<BillReminder> findDueSoon(int userId, LocalDate fromDate, LocalDate toDate) throws SQLException {
        List<BillReminder> reminders = new ArrayList<>();
        for (BillReminder reminder : findByUser(userId)) {
            if (!reminder.isActive() || reminder.getNextDueDate() == null) {
                continue;
            }
            LocalDate nextDueDate = reminder.getNextDueDate();
            if ((nextDueDate.isEqual(fromDate) || nextDueDate.isAfter(fromDate))
                    && (nextDueDate.isEqual(toDate) || nextDueDate.isBefore(toDate))) {
                reminders.add(reminder);
            }
        }
        reminders.sort(Comparator.comparing(BillReminder::getNextDueDate));
        return reminders;
    }

    public BillReminder findById(int id, int userId) throws SQLException {
        String sql = "SELECT * FROM bill_reminders WHERE id = ? AND user_id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    BillReminder reminder = map(rs);
                    decorateDates(reminder, LocalDate.now());
                    return reminder;
                }
            }
        }
        return null;
    }

    public void insert(BillReminder reminder) throws SQLException {
        String sql = "INSERT INTO bill_reminders (user_id, bill_name, amount, due_date, due_day, frequency, notes, status) VALUES (?,?,?,?,?,?,?,?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, reminder.getUserId());
            ps.setString(2, reminder.getBillName());
            ps.setBigDecimal(3, reminder.getAmount());
            ps.setDate(4, reminder.getDueDate() != null ? Date.valueOf(reminder.getDueDate()) : null);
            ps.setInt(5, reminder.getDueDay());
            ps.setString(6, reminder.getFrequency());
            ps.setString(7, reminder.getNotes());
            ps.setString(8, reminder.getStatus() != null ? reminder.getStatus() : "active");
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    reminder.setId(keys.getInt(1));
                }
            }
        }
    }

    public void update(BillReminder reminder) throws SQLException {
        String sql = "UPDATE bill_reminders SET bill_name = ?, amount = ?, due_date = ?, due_day = ?, frequency = ?, notes = ?, status = ? WHERE id = ? AND user_id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, reminder.getBillName());
            ps.setBigDecimal(2, reminder.getAmount());
            ps.setDate(3, reminder.getDueDate() != null ? Date.valueOf(reminder.getDueDate()) : null);
            ps.setInt(4, reminder.getDueDay());
            ps.setString(5, reminder.getFrequency());
            ps.setString(6, reminder.getNotes());
            ps.setString(7, reminder.getStatus());
            ps.setInt(8, reminder.getId());
            ps.setInt(9, reminder.getUserId());
            ps.executeUpdate();
        }
    }

    public void delete(int id, int userId) throws SQLException {
        String sql = "DELETE FROM bill_reminders WHERE id = ? AND user_id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

    public void updateStatus(int id, int userId, String status) throws SQLException {
        String sql = "UPDATE bill_reminders SET status = ? WHERE id = ? AND user_id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, id);
            ps.setInt(3, userId);
            ps.executeUpdate();
        }
    }

    private static BillReminder map(ResultSet rs) throws SQLException {
        BillReminder reminder = new BillReminder();
        reminder.setId(rs.getInt("id"));
        reminder.setUserId(rs.getInt("user_id"));
        reminder.setBillName(rs.getString("bill_name"));
        reminder.setAmount(rs.getBigDecimal("amount"));
        Date dueDate = rs.getDate("due_date");
        if (dueDate != null) {
            reminder.setDueDate(dueDate.toLocalDate());
        }
        reminder.setDueDay(rs.getInt("due_day"));
        reminder.setFrequency(rs.getString("frequency"));
        reminder.setNotes(rs.getString("notes"));
        reminder.setStatus(rs.getString("status"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            reminder.setCreatedAt(ts.toLocalDateTime());
        }
        return reminder;
    }

    private static void decorateDates(BillReminder reminder, LocalDate today) {
        LocalDate nextDueDate = calculateNextDueDate(reminder.getDueDate(), reminder.getDueDay(), reminder.getFrequency(), today);
        reminder.setNextDueDate(nextDueDate);
        reminder.setDaysUntilDue(ChronoUnit.DAYS.between(today, nextDueDate));
    }

    private static LocalDate calculateNextDueDate(LocalDate dueDate, int dueDay, String frequency, LocalDate today) {
        if (dueDate == null) {
            return calculateMonthlyDueDate(today, dueDay);
        }
        if ("yearly".equalsIgnoreCase(frequency)) {
            return calculateYearlyDueDate(today, dueDate);
        }
        return calculateMonthlyDueDate(today, dueDate.getDayOfMonth());
    }

    private static LocalDate calculateMonthlyDueDate(LocalDate today, int dueDay) {
        int normalizedDueDay = Math.max(1, dueDay);
        int safeDueDay = Math.min(normalizedDueDay, YearMonth.from(today).lengthOfMonth());
        LocalDate currentMonthDue = today.withDayOfMonth(safeDueDay);
        if (currentMonthDue.isBefore(today)) {
            LocalDate nextMonth = today.plusMonths(1);
            int nextMonthDay = Math.min(normalizedDueDay, YearMonth.from(nextMonth).lengthOfMonth());
            return nextMonth.withDayOfMonth(nextMonthDay);
        }
        return currentMonthDue;
    }

    private static LocalDate calculateYearlyDueDate(LocalDate today, LocalDate dueDate) {
        int targetMonth = dueDate.getMonthValue();
        int targetDay = dueDate.getDayOfMonth();
        LocalDate currentYearDue = createSafeDate(today.getYear(), targetMonth, targetDay);
        if (currentYearDue.isBefore(today)) {
            return createSafeDate(today.getYear() + 1, targetMonth, targetDay);
        }
        return currentYearDue;
    }

    private static LocalDate createSafeDate(int year, int month, int day) {
        YearMonth yearMonth = YearMonth.of(year, month);
        return LocalDate.of(year, month, Math.min(day, yearMonth.lengthOfMonth()));
    }
}
