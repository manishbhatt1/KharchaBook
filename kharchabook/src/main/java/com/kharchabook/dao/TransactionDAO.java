package com.kharchabook.dao;

import com.kharchabook.model.TransactionRecord;
import com.kharchabook.util.DBUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO class for handling all database operations related to transactions.
 * Includes CRUD operations and analytical queries (summary, trends, reports).
 */
public class TransactionDAO {

    /**
     * Insert a new transaction into database.
     */
    public void insert(TransactionRecord t) throws SQLException {
        String sql = "INSERT INTO transactions (user_id, category_id, type, amount, description, transaction_date) VALUES (?,?,?,?,?,?)";

        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, t.getUserId());
            ps.setInt(2, t.getCategoryId());
            ps.setString(3, t.getType());
            ps.setBigDecimal(4, t.getAmount());
            ps.setString(5, t.getDescription());
            ps.setDate(6, Date.valueOf(t.getTransactionDate()));

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    t.setId(keys.getInt(1));
                }
            }
        }
    }

    /**
     * Update an existing transaction.
     */
    public void update(TransactionRecord t) throws SQLException {
        String sql = "UPDATE transactions SET category_id = ?, type = ?, amount = ?, description = ?, transaction_date = ? WHERE id = ? AND user_id = ?";

        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, t.getCategoryId());
            ps.setString(2, t.getType());
            ps.setBigDecimal(3, t.getAmount());
            ps.setString(4, t.getDescription());
            ps.setDate(5, Date.valueOf(t.getTransactionDate()));
            ps.setInt(6, t.getId());
            ps.setInt(7, t.getUserId());

            ps.executeUpdate();
        }
    }

    /**
     * Delete transaction by id and user.
     */
    public void delete(int id, int userId) throws SQLException {
        String sql = "DELETE FROM transactions WHERE id = ? AND user_id = ?";

        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.setInt(2, userId);

            ps.executeUpdate();
        }
    }

    /**
     * Find transaction by ID for a specific user.
     */
    public TransactionRecord findById(int id, int userId) throws SQLException {
        String sql = "SELECT t.*, c.name AS category_name FROM transactions t " +
                     "JOIN categories c ON c.id = t.category_id " +
                     "WHERE t.id = ? AND t.user_id = ?";

        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.setInt(2, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapJoin(rs);
                }
            }
        }
        return null;
    }

    /**
     * Fetch filtered transactions for a user.
     */
    public List<TransactionRecord> findForUser(int userId, Integer year, Integer month, String type, Integer categoryId) throws SQLException {
        List<TransactionRecord> list = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
                "SELECT t.*, c.name AS category_name FROM transactions t JOIN categories c ON c.id = t.category_id WHERE t.user_id = ?"
        );

        List<Object> params = new ArrayList<>();
        params.add(userId);

        if (year != null) {
            sql.append(" AND YEAR(t.transaction_date) = ?");
            params.add(year);
        }
        if (month != null) {
            sql.append(" AND MONTH(t.transaction_date) = ?");
            params.add(month);
        }
        if (type != null && !type.isEmpty() && !"all".equalsIgnoreCase(type)) {
            sql.append(" AND t.type = ?");
            params.add(type);
        }
        if (categoryId != null && categoryId > 0) {
            sql.append(" AND t.category_id = ?");
            params.add(categoryId);
        }

        sql.append(" ORDER BY t.transaction_date DESC, t.id DESC");

        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql.toString())) {

            for (int i = 0; i < params.size(); i++) {
                Object p = params.get(i);
                if (p instanceof Integer) {
                    ps.setInt(i + 1, (Integer) p);
                } else {
                    ps.setString(i + 1, p.toString());
                }
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapJoin(rs));
                }
            }
        }

        return list;
    }

    /**
     * Sum income for a month.
     */
    public BigDecimal sumIncomeForMonth(int userId, int year, int month) throws SQLException {
        return sumTypeForMonth(userId, year, month, "income");
    }

    /**
     * Sum expense for a month.
     */
    public BigDecimal sumExpenseForMonth(int userId, int year, int month) throws SQLException {
        return sumTypeForMonth(userId, year, month, "expense");
    }

    public BigDecimal totalIncomeYearMonth(int userId, int year, int month) throws SQLException {
        return sumIncomeForMonth(userId, year, month);
    }

    public BigDecimal totalExpenseYearMonth(int userId, int year, int month) throws SQLException {
        return sumExpenseForMonth(userId, year, month);
    }

    public int countTotalInSystem() throws SQLException {
        String sql = "SELECT COUNT(*) FROM transactions";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public int topCategoryByTransactionCount() throws SQLException {
        String sql = "SELECT category_id FROM transactions GROUP BY category_id ORDER BY COUNT(*) DESC, category_id ASC LIMIT 1";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        }
    }

    public List<TransactionRecord> findRecent(int userId, int limit) throws SQLException {
        List<TransactionRecord> list = new ArrayList<>();
        String sql = "SELECT t.*, c.name AS category_name FROM transactions t " +
                "JOIN categories c ON c.id = t.category_id " +
                "WHERE t.user_id = ? ORDER BY t.transaction_date DESC, t.id DESC LIMIT ?";

        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, Math.max(1, limit));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapJoin(rs));
                }
            }
        }
        return list;
    }

    /**
     * Sum expense for a specific day.
     */
    public BigDecimal sumExpenseForDay(int userId, LocalDate date) throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount),0) FROM transactions WHERE user_id = ? AND type = 'expense' AND transaction_date = ?";

        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(date));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                }
            }
        }
        return BigDecimal.ZERO;
    }

    /**
     * Count distinct expense categories for a day.
     */
    public int countExpenseCategoriesForDay(int userId, LocalDate date) throws SQLException {
        String sql = "SELECT COUNT(DISTINCT category_id) FROM transactions WHERE user_id = ? AND type = 'expense' AND transaction_date = ?";

        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setDate(2, Date.valueOf(date));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    /**
     * Helper method to sum income/expense by type for month.
     */
    private BigDecimal sumTypeForMonth(int userId, int year, int month, String type) throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount),0) FROM transactions WHERE user_id = ? AND type = ? AND YEAR(transaction_date) = ? AND MONTH(transaction_date) = ?";

        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, type);
            ps.setInt(3, year);
            ps.setInt(4, month);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                }
            }
        }
        return BigDecimal.ZERO;
    }

    /**
     * Expense grouped by category for a month.
     */
    public java.util.Map<Integer, BigDecimal> expenseByCategoryForMonth(int userId, int year, int month) throws SQLException {
        java.util.Map<Integer, BigDecimal> map = new java.util.HashMap<>();

        String sql = "SELECT category_id, COALESCE(SUM(amount),0) AS s FROM transactions WHERE user_id = ? AND type = 'expense' AND YEAR(transaction_date) = ? AND MONTH(transaction_date) = ? GROUP BY category_id";

        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, year);
            ps.setInt(3, month);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    map.put(rs.getInt("category_id"), rs.getBigDecimal("s"));
                }
            }
        }

        return map;
    }

    /**
     * Find top expense category for month.
     */
    public int topExpenseCategoryIdForMonth(int userId, int year, int month) throws SQLException {
        String sql = "SELECT category_id FROM transactions WHERE user_id = ? AND type = 'expense' AND YEAR(transaction_date) = ? AND MONTH(transaction_date) = ? GROUP BY category_id ORDER BY SUM(amount) DESC LIMIT 1";

        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, year);
            ps.setInt(3, month);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }

        return 0;
    }

    /**
     * Map ResultSet to TransactionRecord object.
     */
    private static TransactionRecord mapJoin(ResultSet rs) throws SQLException {
        TransactionRecord t = new TransactionRecord();
        t.setId(rs.getInt("id"));
        t.setUserId(rs.getInt("user_id"));
        t.setCategoryId(rs.getInt("category_id"));
        t.setCategoryName(rs.getString("category_name"));
        t.setType(rs.getString("type"));
        t.setAmount(rs.getBigDecimal("amount"));
        t.setDescription(rs.getString("description"));

        Date d = rs.getDate("transaction_date");
        if (d != null) {
            t.setTransactionDate(d.toLocalDate());
        }

        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            t.setCreatedAt(ts.toLocalDateTime());
        }

        return t;
    }

    public BigDecimal sumExpenseForCategoryMonth(int userId, int categoryId, int year, int month) throws SQLException {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM transactions WHERE user_id = ? AND category_id = ? AND type = 'expense' AND YEAR(transaction_date) = ? AND MONTH(transaction_date) = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, categoryId);
            ps.setInt(3, year);
            ps.setInt(4, month);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal(1);
                }
            }
        }
        return BigDecimal.ZERO;
    }
}
