package com.kharchabook.dao;

import com.kharchabook.model.Budget;
import com.kharchabook.util.DBUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BudgetDAO {

    public List<Budget> findForUserMonth(int userId, int year, int month, TransactionDAO txDao) throws SQLException {
        List<Budget> list = new ArrayList<>();
        String sql = "SELECT b.*, c.name AS category_name FROM budgets b JOIN categories c ON c.id = b.category_id WHERE b.user_id = ? AND b.year = ? AND b.month = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, year);
            ps.setInt(3, month);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Budget b = map(rs);
                    BigDecimal spent = txDao.sumExpenseForCategoryMonth(userId, b.getCategoryId(), year, month);
                    b.setSpentAmount(spent);
                    list.add(b);
                }
            }
        }
        return list;
    }

    public void insert(Budget b) throws SQLException {
        String sql = "INSERT INTO budgets (user_id, category_id, monthly_limit, month, year) VALUES (?,?,?,?,?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, b.getUserId());
            ps.setInt(2, b.getCategoryId());
            ps.setBigDecimal(3, b.getMonthlyLimit());
            ps.setInt(4, b.getMonth());
            ps.setInt(5, b.getYear());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    b.setId(keys.getInt(1));
                }
            }
        }
    }

    public void update(int id, int userId, BigDecimal limit) throws SQLException {
        String sql = "UPDATE budgets SET monthly_limit = ? WHERE id = ? AND user_id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setBigDecimal(1, limit);
            ps.setInt(2, id);
            ps.setInt(3, userId);
            ps.executeUpdate();
        }
    }

    public void delete(int id, int userId) throws SQLException {
        String sql = "DELETE FROM budgets WHERE id = ? AND user_id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

    public Budget findById(int id, int userId) throws SQLException {
        String sql = "SELECT b.*, c.name AS category_name FROM budgets b JOIN categories c ON c.id = b.category_id WHERE b.id = ? AND b.user_id = ?";
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

    private static Budget map(ResultSet rs) throws SQLException {
        Budget b = new Budget();
        b.setId(rs.getInt("id"));
        b.setUserId(rs.getInt("user_id"));
        b.setCategoryId(rs.getInt("category_id"));
        b.setCategoryName(rs.getString("category_name"));
        b.setMonthlyLimit(rs.getBigDecimal("monthly_limit"));
        b.setMonth(rs.getInt("month"));
        b.setYear(rs.getInt("year"));
        return b;
    }
}
