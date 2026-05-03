package com.kharchabook.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import com.kharchabook.model.Category;
import com.kharchabook.util.DBUtil;

/**
 * Data Access Object for Category entities.
 * Handles database operations for categories including CRUD and transaction counting.
 */
public class CategoryDAO {

    /**
     * Finds all categories of a specific type (e.g., "INCOME" or "EXPENSE").
     * Results are ordered alphabetically by name.
     * 
     * @param type the category type to filter by
     * @return list of matching categories
     * @throws SQLException if a database error occurs
     */
    public List<Category> findByType(String type) throws SQLException {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM categories WHERE type = ? ORDER BY name";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, type);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    /**
     * Retrieves all categories from the database, ordered by type then name.
     * Also includes the transaction count for each category via a subquery.
     * 
     * @return list of all categories with their transaction counts
     * @throws SQLException if a database error occurs
     */
    public List<Category> findAll() throws SQLException {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT c.*, (SELECT COUNT(*) FROM transactions t WHERE t.category_id = c.id) AS tx_count FROM categories c ORDER BY c.type, c.name";
        try (Connection c = DBUtil.getConnection();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                Category cat = map(rs);
                cat.setTransactionCount(rs.getLong("tx_count")); // Set the computed transaction count
                list.add(cat);
            }
        }
        return list;
    }

    /**
     * Finds a single category by its unique ID.
     * 
     * @param id the category ID to search for
     * @return the Category object if found, null otherwise
     * @throws SQLException if a database error occurs
     */
    public Category findById(int id) throws SQLException {
        String sql = "SELECT * FROM categories WHERE id = ?";
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

    /**
     * Counts how many transactions are associated with a given category.
     * Used to determine if a category can be safely deleted.
     * 
     * @param categoryId the category ID to check
     * @return number of transactions linked to this category
     * @throws SQLException if a database error occurs
     */
    public long countTransactions(int categoryId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM transactions WHERE category_id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, categoryId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getLong(1);
                }
            }
        }
        return 0;
    }

    /**
     * Inserts a new category into the database.
     * The generated ID is set back into the Category object.
     * 
     * @param cat the Category object to insert (must have name, type, icon, and optionally created_by)
     * @throws SQLException if a database error occurs
     */
    public void insert(Category cat) throws SQLException {
        String sql = "INSERT INTO categories (name, type, icon, created_by) VALUES (?,?,?,?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, cat.getName());
            ps.setString(2, cat.getType());
            ps.setString(3, cat.getIcon());
            // Handle nullable created_by field
            if (cat.getCreatedBy() != null) {
                ps.setInt(4, cat.getCreatedBy());
            } else {
                ps.setNull(4, Types.INTEGER);
            }
            ps.executeUpdate();
            // Retrieve and set the auto-generated primary key
            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    cat.setId(keys.getInt(1));
                }
            }
        }
    }

    /**
     * Updates an existing category's name and type.
     * Note: Icon and created_by are not updated by this method.
     * 
     * @param id the ID of the category to update
     * @param name the new name for the category
     * @param type the new type for the category
     * @throws SQLException if a database error occurs
     */
    public void update(int id, String name, String type) throws SQLException {
        String sql = "UPDATE categories SET name = ?, type = ? WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, type);
            ps.setInt(3, id);
            ps.executeUpdate();
        }
    }

    /**
     * Deletes a category only if it has no associated transactions.
     * Prevents orphaned transaction references.
     * 
     * @param id the ID of the category to delete
     * @throws SQLException if the category has transactions or a database error occurs
     */
    public void deleteIfUnused(int id) throws SQLException {
        // Safety check: prevent deletion if transactions exist
        if (countTransactions(id) > 0) {
            throw new SQLException("Category has transactions");
        }
        String sql = "DELETE FROM categories WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    /**
     * Maps the current row of a ResultSet to a Category object.
     * Handles nullable created_by field gracefully.
     * 
     * @param rs the ResultSet positioned at a valid row
     * @return a populated Category object
     * @throws SQLException if a column access error occurs
     */
    private static Category map(ResultSet rs) throws SQLException {
        Category c = new Category();
        c.setId(rs.getInt("id"));
        c.setName(rs.getString("name"));
        c.setType(rs.getString("type"));
        c.setIcon(rs.getString("icon"));
        int cb = rs.getInt("created_by");
        if (!rs.wasNull()) { // Only set if the column had a non-null value
            c.setCreatedBy(cb);
        }
        return c;
    }
}