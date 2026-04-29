package com.kharchabook.dao;

import com.kharchabook.model.User;
import com.kharchabook.util.DBUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    public User findByEmailOrPhone(String login) throws SQLException {
        String sql = "SELECT * FROM users WHERE email = ? OR phone = ? LIMIT 1";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, login.trim());
            ps.setString(2, login.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    public User findById(int id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    public boolean emailExists(String email, Integer excludeId) throws SQLException {
        String sql = excludeId == null
                ? "SELECT 1 FROM users WHERE email = ?"
                : "SELECT 1 FROM users WHERE email = ? AND id <> ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, email.trim());
            if (excludeId != null) {
                ps.setInt(2, excludeId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public boolean phoneExists(String phone, Integer excludeId) throws SQLException {
        String sql = excludeId == null
                ? "SELECT 1 FROM users WHERE phone = ?"
                : "SELECT 1 FROM users WHERE phone = ? AND id <> ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, phone.trim());
            if (excludeId != null) {
                ps.setInt(2, excludeId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public void insert(User u) throws SQLException {
        String sql = "INSERT INTO users (full_name, email, phone, password, role, status) VALUES (?,?,?,?,?,?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getFullName());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getPhone());
            ps.setString(4, u.getPassword());
            ps.setString(5, u.getRole());
            ps.setString(6, u.getStatus());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    u.setId(keys.getInt(1));
                }
            }
        }
    }

    public void updateProfile(int id, String fullName, String phone, String email) throws SQLException {
        String sql = "UPDATE users SET full_name = ?, phone = ?, email = ? WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, fullName);
            ps.setString(2, phone);
            ps.setString(3, email);
            ps.setInt(4, id);
            ps.executeUpdate();
        }
    }

    public void updatePassword(int id, String passwordHash) throws SQLException {
        String sql = "UPDATE users SET password = ? WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, passwordHash);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    public void setStatus(int id, String status) throws SQLException {
        String sql = "UPDATE users SET status = ? WHERE id = ? AND role = 'USER'";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setInt(2, id);
            ps.executeUpdate();
        }
    }

    public List<User> findAllUsers(String search) throws SQLException {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users WHERE role = 'USER'";
        if (search != null && !search.trim().isEmpty()) {
            sql += " AND (full_name LIKE ? OR phone LIKE ?)";
        }
        sql += " ORDER BY created_at DESC";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            if (search != null && !search.trim().isEmpty()) {
                String q = "%" + search.trim() + "%";
                ps.setString(1, q);
                ps.setString(2, q);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    public int countRegistrationsInMonth(int year, int month) throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE role = 'USER' AND YEAR(created_at) = ? AND MONTH(created_at) = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, year);
            ps.setInt(2, month);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public int countTransactionsForUser(int userId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM transactions WHERE user_id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }

    public int countRegularUsers() throws SQLException {
        String sql = "SELECT COUNT(*) FROM users WHERE role = 'USER'";
        try (Connection c = DBUtil.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    /** months 1–12 counts for registrations in given year */
    public int[] registrationCountsForYear(int year) throws SQLException {
        int[] months = new int[12];
        String sql = "SELECT MONTH(created_at) AS m, COUNT(*) AS c FROM users WHERE role = 'USER' AND YEAR(created_at) = ? GROUP BY MONTH(created_at)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, year);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int m = rs.getInt("m");
                    if (m >= 1 && m <= 12) {
                        months[m - 1] = rs.getInt("c");
                    }
                }
            }
        }
        return months;
    }

    /** rows: id, full_name, transaction_count */
    public List<Object[]> topActiveUsers(int limit) throws SQLException {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT u.id, u.full_name, COUNT(t.id) AS c FROM users u LEFT JOIN transactions t ON t.user_id = u.id WHERE u.role = 'USER' GROUP BY u.id, u.full_name ORDER BY c DESC LIMIT ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{rs.getInt("id"), rs.getString("full_name"), rs.getLong("c")});
                }
            }
        }
        return list;
    }

    private static User map(ResultSet rs) throws SQLException {
        User u = new User();
        u.setId(rs.getInt("id"));
        u.setFullName(rs.getString("full_name"));
        u.setEmail(rs.getString("email"));
        u.setPhone(rs.getString("phone"));
        u.setPassword(rs.getString("password"));
        u.setRole(rs.getString("role"));
        u.setStatus(rs.getString("status"));
        Timestamp ts = rs.getTimestamp("created_at");
        if (ts != null) {
            u.setCreatedAt(ts.toLocalDateTime());
        }
        return u;
    }
}
