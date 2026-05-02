package com.kharchabook.dao;

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

    public void updateTokenHashAndExpiry(int id, String tokenHash, LocalDateTime expiry) throws SQLException {
        String sql = "UPDATE remember_tokens SET token_hash = ?, expires_at = ? WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, tokenHash);
            ps.setTimestamp(2, Timestamp.valueOf(expiry));
            ps.setInt(3, id);
            ps.executeUpdate();
        }
    }

    public void deleteExpired() throws SQLException {
        String sql = "DELETE FROM remember_tokens WHERE expires_at < NOW()";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.executeUpdate();
        }
    }

    public void upsert(int userId, String selector, String tokenHash, LocalDateTime expiresAt) throws SQLException {
        String sql = "INSERT INTO remember_tokens (user_id, selector, token_hash, expires_at) VALUES (?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE user_id = VALUES(user_id), token_hash = VALUES(token_hash), expires_at = VALUES(expires_at)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setString(2, selector);
            ps.setString(3, tokenHash);
            ps.setTimestamp(4, Timestamp.valueOf(expiresAt));
            ps.executeUpdate();
        }
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
