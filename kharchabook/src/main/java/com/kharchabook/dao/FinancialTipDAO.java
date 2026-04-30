package com.kharchabook.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.kharchabook.model.FinancialTip;
import com.kharchabook.util.DBUtil;

public class FinancialTipDAO {

    public List<FinancialTip> search(String keyword, String category, int currentUserId) throws SQLException {
        List<FinancialTip> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder(
                "SELECT t.*, (SELECT COUNT(*) FROM wishlist w WHERE w.tip_id = t.id) AS wl_count, " +
                        "(SELECT COUNT(*) FROM wishlist w2 WHERE w2.tip_id = t.id AND w2.user_id = ?) AS mine " +
                        "FROM financial_tips t WHERE 1=1");
        List<Object> params = new ArrayList<>();
        params.add(currentUserId);
        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append(" AND (t.title LIKE ? OR t.content LIKE ?)");
            String q = "%" + keyword.trim() + "%";
            params.add(q);
            params.add(q);
        }
        if (category != null && !category.isEmpty() && !"all".equalsIgnoreCase(category)) {
            sql.append(" AND t.category = ?");
            params.add(category);
        }
        sql.append(" ORDER BY t.posted_date DESC");
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    FinancialTip tip = map(rs);
                    tip.setWishlistCount(rs.getLong("wl_count"));
                    tip.setWishlistedByCurrentUser(rs.getLong("mine") > 0);
                    list.add(tip);
                }
            }
        }
        return list;
    }

    public FinancialTip findByIdForAdmin(int id) throws SQLException {
        String sql = "SELECT * FROM financial_tips WHERE id = ?";
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

    public FinancialTip findById(int id, int currentUserId) throws SQLException {
        String sql = "SELECT t.*, (SELECT COUNT(*) FROM wishlist w WHERE w.tip_id = t.id) AS wl_count, " +
                "(SELECT COUNT(*) FROM wishlist w2 WHERE w2.tip_id = t.id AND w2.user_id = ?) AS mine " +
                "FROM financial_tips t WHERE t.id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, currentUserId);
            ps.setInt(2, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    FinancialTip tip = map(rs);
                    tip.setWishlistCount(rs.getLong("wl_count"));
                    tip.setWishlistedByCurrentUser(rs.getLong("mine") > 0);
                    return tip;
                }
            }
        }
        return null;
    }

    public void insert(FinancialTip tip) throws SQLException {
        String sql = "INSERT INTO financial_tips (title, content, category, posted_by) VALUES (?,?,?,?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, tip.getTitle());
            ps.setString(2, tip.getContent());
            ps.setString(3, tip.getCategory());
            ps.setInt(4, tip.getPostedBy());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    tip.setId(keys.getInt(1));
                }
            }
        }
    }

    public void update(int id, String title, String content, String category) throws SQLException {
        String sql = "UPDATE financial_tips SET title = ?, content = ?, category = ? WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, content);
            ps.setString(3, category);
            ps.setInt(4, id);
            ps.executeUpdate();
        }
    }

    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM financial_tips WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    public List<FinancialTip> findAllForAdmin() throws SQLException {
        List<FinancialTip> list = new ArrayList<>();
        String sql = "SELECT t.*, (SELECT COUNT(*) FROM wishlist w WHERE w.tip_id = t.id) AS wl_count FROM financial_tips t ORDER BY t.posted_date DESC";
        try (Connection c = DBUtil.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                FinancialTip tip = map(rs);
                tip.setWishlistCount(rs.getLong("wl_count"));
                list.add(tip);
            }
        }
        return list;
    }

    public List<Object[]> mostWishlistedTips(int limit) throws SQLException {
        List<Object[]> list = new ArrayList<>();
        String sql = "SELECT t.id, t.title, COUNT(w.id) AS c FROM financial_tips t LEFT JOIN wishlist w ON w.tip_id = t.id GROUP BY t.id, t.title ORDER BY c DESC LIMIT ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Object[]{rs.getInt("id"), rs.getString("title"), rs.getLong("c")});
                }
            }
        }
        return list;
    }

    private static FinancialTip map(ResultSet rs) throws SQLException {
        FinancialTip t = new FinancialTip();
        t.setId(rs.getInt("id"));
        t.setTitle(rs.getString("title"));
        t.setContent(rs.getString("content"));
        t.setCategory(rs.getString("category"));
        t.setPostedBy(rs.getInt("posted_by"));
        Timestamp ts = rs.getTimestamp("posted_date");
        if (ts != null) {
            t.setPostedDate(ts.toLocalDateTime());
        }
        return t;
    }
}
