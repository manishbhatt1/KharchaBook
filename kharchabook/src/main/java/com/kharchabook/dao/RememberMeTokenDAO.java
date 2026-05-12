package com.kharchabook.dao;

import com.kharchabook.model.RememberMeToken;
import com.kharchabook.util.DBUtil;

import java.sql.*;
import java.time.LocalDateTime;

public class RememberMeTokenDAO {
    
    public TokenRecord findBySelector(String selector) throws SQLException {
        String sql = "SELECT id, user_id, token_hash, expires_at FROM remember_tokens WHERE selector = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, selector);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Timestamp expiresTs = rs.getTimestamp("expires_at");
                    LocalDateTime expiresAt = expiresTs != null ? expiresTs.toLocalDateTime() : null;
                    return new TokenRecord(rs.getInt("id"), rs.getInt("user_id"), rs.getString("token_hash"), expiresAt);
                }
            }
        }
        return null;
    }

    public void deleteBySelector(String selector) throws SQLException {
        String sql = "DELETE FROM remember_tokens WHERE selector = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, selector);
            ps.executeUpdate();
        }
    }

    public void deleteExpired() throws SQLException {
        String sql = "DELETE FROM remember_tokens WHERE expires_at <= ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
            ps.executeUpdate();
        }
    }

    public void upsert(int userId, String selector, String tokenHash, LocalDateTime expiresAt) throws SQLException {
        String deleteSql = "DELETE FROM remember_tokens WHERE user_id = ?";
        String insertSql = "INSERT INTO remember_tokens (user_id, selector, token_hash, expires_at) VALUES (?, ?, ?, ?)";
        try (Connection c = DBUtil.getConnection()) {
            c.setAutoCommit(false);
            try (PreparedStatement delete = c.prepareStatement(deleteSql);
                 PreparedStatement insert = c.prepareStatement(insertSql)) {
                delete.setInt(1, userId);
                delete.executeUpdate();

                insert.setInt(1, userId);
                insert.setString(2, selector);
                insert.setString(3, tokenHash);
                insert.setTimestamp(4, Timestamp.valueOf(expiresAt));
                insert.executeUpdate();

                c.commit();
            } catch (SQLException e) {
                c.rollback();
                throw e;
            } finally {
                c.setAutoCommit(true);
            }
        }
    }

    public void updateTokenHashAndExpiry(int id, String tokenHash, LocalDateTime expiresAt) throws SQLException {
        String sql = "UPDATE remember_tokens SET token_hash = ?, expires_at = ? WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, tokenHash);
            ps.setTimestamp(2, Timestamp.valueOf(expiresAt));
            ps.setInt(3, id);
            ps.executeUpdate();
        }
    }

    private static RememberMeToken map(ResultSet rs) throws SQLException {
        RememberMeToken token = new RememberMeToken();
        token.setId(rs.getInt("id"));
        token.setUserId(rs.getInt("user_id"));
        token.setSelector(rs.getString("selector"));
        token.setValidator(rs.getString("token_hash"));
        Timestamp ts = rs.getTimestamp("expires_at");
        if (ts != null) {
            token.setExpiresAt(ts.toLocalDateTime());
        }
        ts = rs.getTimestamp("created_at");
        if (ts != null) {
            token.setCreatedAt(ts.toLocalDateTime());
        }
        return token;
    }

    public static class TokenRecord {
        public int id;
        public int userId;
        public String tokenHash;
        public LocalDateTime expiresAt;
        
        public TokenRecord(int id, int userId, String tokenHash, LocalDateTime expiresAt) {
            this.id = id;
            this.userId = userId;
            this.tokenHash = tokenHash;
            this.expiresAt = expiresAt;
        }
    }
}
