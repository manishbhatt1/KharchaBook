package com.kharchabook.dao;

import com.kharchabook.model.RememberMeToken;
import com.kharchabook.util.DBUtil;

import java.sql.*;
import java.time.LocalDateTime;

public class RememberMeTokenDAO {
    
    public TokenRecord findBySelector(String selector) throws SQLException {
        String sql = "SELECT id, user_id, validator, expires_at FROM remember_me_tokens WHERE selector = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, selector);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Timestamp expiresTs = rs.getTimestamp("expires_at");
                    LocalDateTime expiresAt = expiresTs != null ? expiresTs.toLocalDateTime() : null;
                    return new TokenRecord(rs.getInt("id"), rs.getInt("user_id"), rs.getString("validator"), expiresAt);
                }
            }
        }
        return null;
    }

    public void deleteBySelector(String selector) throws SQLException {
        String sql = "DELETE FROM remember_me_tokens WHERE selector = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, selector);
            ps.executeUpdate();
        }
    }

    private static RememberMeToken map(ResultSet rs) throws SQLException {
        RememberMeToken token = new RememberMeToken();
        token.setId(rs.getInt("id"));
        token.setUserId(rs.getInt("user_id"));
        token.setSelector(rs.getString("selector"));
        token.setValidator(rs.getString("validator"));
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
