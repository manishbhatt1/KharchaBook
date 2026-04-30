package com.kharchabook.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.kharchabook.model.SavingsGoal;
import com.kharchabook.util.DBUtil;

public class SavingsGoalDAO {

    public List<SavingsGoal> findByUserAndStatus(int userId, String status) throws SQLException {
        List<SavingsGoal> list = new ArrayList<>();
        String sql = "SELECT * FROM savings_goals WHERE user_id = ? AND status = ? ORDER BY deadline IS NULL, deadline, id";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    public List<SavingsGoal> findDueSoonActiveGoals(int userId, LocalDate fromDate, LocalDate toDate) throws SQLException {
        List<SavingsGoal> list = new ArrayList<>();
        String sql = "SELECT * FROM savings_goals WHERE user_id = ? AND status = 'active' AND deadline IS NOT NULL AND deadline BETWEEN ? AND ? ORDER BY deadline, id";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(fromDate));
            ps.setDate(3, Date.valueOf(toDate));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    public BigDecimal sumSavedAmountByStatus(int userId, String status) throws SQLException {
        String sql = "SELECT COALESCE(SUM(saved_amount), 0) FROM savings_goals WHERE user_id = ? AND status = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, status);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                }
            }
        }
        return BigDecimal.ZERO;
    }

    public SavingsGoal findById(int id, int userId) throws SQLException {
        String sql = "SELECT * FROM savings_goals WHERE id = ? AND user_id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    public void insert(SavingsGoal g) throws SQLException {
        String sql = "INSERT INTO savings_goals (user_id, title, target_amount, saved_amount, deadline, status) VALUES (?,?,?,?,?,?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, g.getUserId());
            ps.setString(2, g.getTitle());
            ps.setBigDecimal(3, g.getTargetAmount());
            ps.setBigDecimal(4, g.getSavedAmount() != null ? g.getSavedAmount() : BigDecimal.ZERO);
            if (g.getDeadline() != null) {
                ps.setDate(5, Date.valueOf(g.getDeadline()));
            } else {
                ps.setNull(5, Types.DATE);
            }
            ps.setString(6, g.getStatus() != null ? g.getStatus() : "active");
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    g.setId(keys.getInt(1));
                }
            }
        }
    }

    public void addSavedAmount(int id, int userId, BigDecimal add) throws SQLException {
        String sql = "UPDATE savings_goals SET saved_amount = saved_amount + ? WHERE id = ? AND user_id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBigDecimal(1, add);
            ps.setInt(2, id);
            ps.setInt(3, userId);
            ps.executeUpdate();
        }
    }

    public void updateGoal(int id, int userId, String title, BigDecimal target, LocalDate deadline) throws SQLException {
        String sql = "UPDATE savings_goals SET title = ?, target_amount = ?, deadline = ? WHERE id = ? AND user_id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setBigDecimal(2, target);
            if (deadline != null) {
                ps.setDate(3, Date.valueOf(deadline));
            } else {
                ps.setNull(3, Types.DATE);
            }
            ps.setInt(4, id);
            ps.setInt(5, userId);
            ps.executeUpdate();
        }
    }

    public void setStatus(int id, int userId, String status) throws SQLException {
        String sql = "UPDATE savings_goals SET status = ? WHERE id = ? AND user_id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, id);
            ps.setInt(3, userId);
            ps.executeUpdate();
        }
    }

    private static SavingsGoal map(ResultSet rs) throws SQLException {
        SavingsGoal g = new SavingsGoal();
        g.setId(rs.getInt("id"));
        g.setUserId(rs.getInt("user_id"));
        g.setTitle(rs.getString("title"));
        g.setTargetAmount(rs.getBigDecimal("target_amount"));
        g.setSavedAmount(rs.getBigDecimal("saved_amount"));
        Date d = rs.getDate("deadline");
        if (d != null) {
            g.setDeadline(d.toLocalDate());
        }
        g.setStatus(rs.getString("status"));
        return g;
    }
}
