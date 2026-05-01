/**
 * DAO for managing wishlist (saved financial tips) functionality.
 * Handles saving, removing, and fetching saved tips per user.
 */
package com.kharchabook.dao;

import com.kharchabook.model.FinancialTip;
import com.kharchabook.util.DBUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class WishlistDAO {

    /**
     * Add a tip to user's wishlist (ignores duplicate entries).
     */
    public void add(int userId, int tipId) throws SQLException {
        String sql = "INSERT IGNORE INTO wishlist (user_id, tip_id) VALUES (?,?)";

        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, tipId);

            ps.executeUpdate();
        }
    }

    /**
     * Remove a tip from user's wishlist.
     */
    public void remove(int userId, int tipId) throws SQLException {
        String sql = "DELETE FROM wishlist WHERE user_id = ? AND tip_id = ?";

        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setInt(2, tipId);

            ps.executeUpdate();
        }
    }

    /**
     * Get all saved tips for a specific user.
     */
    public List<FinancialTip> findByUser(int userId) throws SQLException {
        List<FinancialTip> list = new ArrayList<>();

        String sql = "SELECT t.*, w.saved_at FROM financial_tips t " +
                     "INNER JOIN wishlist w ON w.tip_id = t.id " +
                     "WHERE w.user_id = ? ORDER BY w.saved_at DESC";

        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    FinancialTip tip = new FinancialTip();
                    tip.setId(rs.getInt("id"));
                    tip.setTitle(rs.getString("title"));
                    tip.setContent(rs.getString("content"));
                    tip.setCategory(rs.getString("category"));
                    tip.setPostedBy(rs.getInt("posted_by"));

                    Timestamp ts = rs.getTimestamp("posted_date");
                    if (ts != null) {
                        tip.setPostedDate(ts.toLocalDateTime());
                    }

                    list.add(tip);
                }
            }
        }

        return list;
    }

    /**
     * Count how many users saved a specific tip.
     */
    public int countForTip(int tipId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM wishlist WHERE tip_id = ?";

        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, tipId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return 0;
    }
}