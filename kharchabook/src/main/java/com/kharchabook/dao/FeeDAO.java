package com.kharchabook.dao;

import com.kharchabook.model.Fee;
import com.kharchabook.util.DBUtil;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class FeeDAO {

    public List<Fee> findCriticalFees(int userId, LocalDate fromDate, int daysAhead) throws SQLException {
        List<Fee> fees = new ArrayList<>();
        LocalDate toDate = fromDate.plusDays(Math.max(0, daysAhead));
        String sql = "SELECT * FROM fees WHERE user_id = ? AND status = 'active' " +
                "AND due_date BETWEEN ? AND ? ORDER BY due_date ASC, id ASC";

        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(fromDate));
            ps.setDate(3, Date.valueOf(toDate));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    fees.add(map(rs, fromDate));
                }
            }
        }
        return fees;
    }

    private static Fee map(ResultSet rs, LocalDate baseDate) throws SQLException {
        Fee fee = new Fee();
        fee.setId(rs.getInt("id"));
        fee.setUserId(rs.getInt("user_id"));
        fee.setFeeName(rs.getString("fee_name"));
        fee.setAmount(rs.getBigDecimal("amount"));
        Date dueDate = rs.getDate("due_date");
        if (dueDate != null) {
            LocalDate due = dueDate.toLocalDate();
            fee.setDueDate(due);
            fee.setDaysUntilDue(ChronoUnit.DAYS.between(baseDate, due));
            fee.setOverdue(due.isBefore(baseDate));
        }
        fee.setDescription(rs.getString("description"));
        fee.setStatus(rs.getString("status"));
        Timestamp created = rs.getTimestamp("created_at");
        if (created != null) {
            fee.setCreatedAt(created.toLocalDateTime());
        }
        Timestamp updated = rs.getTimestamp("updated_at");
        if (updated != null) {
            fee.setUpdatedAt(updated.toLocalDateTime());
        }
        return fee;
    }
}
