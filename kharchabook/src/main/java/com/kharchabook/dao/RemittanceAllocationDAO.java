package com.kharchabook.dao;

import com.kharchabook.model.RemittanceAllocation;
import com.kharchabook.util.DBUtil;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RemittanceAllocationDAO {
    
    public void insert(RemittanceAllocation allocation) throws SQLException {
        String sql = "INSERT INTO remittance_allocations (user_id, total_amount, rent_amount, food_amount, savings_amount, other_amount, description, allocation_date, status, created_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, allocation.getUserId());
            stmt.setBigDecimal(2, allocation.getTotalAmount());
            stmt.setBigDecimal(3, allocation.getRentAmount());
            stmt.setBigDecimal(4, allocation.getFoodAmount());
            stmt.setBigDecimal(5, allocation.getSavingsAmount());
            stmt.setBigDecimal(6, allocation.getOtherAmount());
            stmt.setString(7, allocation.getDescription());
            stmt.setDate(8, allocation.getAllocationDate() != null ? Date.valueOf(allocation.getAllocationDate()) : null);
            stmt.setString(9, allocation.getStatus());
            stmt.setTimestamp(10, allocation.getCreatedAt() != null ? Timestamp.valueOf(allocation.getCreatedAt()) : Timestamp.valueOf(java.time.LocalDateTime.now()));
            
            stmt.executeUpdate();
            
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    allocation.setId(generatedKeys.getInt(1));
                }
            }
        }
    }
    
    public void update(RemittanceAllocation allocation) throws SQLException {
        String sql = "UPDATE remittance_allocations SET total_amount = ?, rent_amount = ?, food_amount = ?, savings_amount = ?, other_amount = ?, description = ?, allocation_date = ?, status = ? WHERE id = ? AND user_id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setBigDecimal(1, allocation.getTotalAmount());
            stmt.setBigDecimal(2, allocation.getRentAmount());
            stmt.setBigDecimal(3, allocation.getFoodAmount());
            stmt.setBigDecimal(4, allocation.getSavingsAmount());
            stmt.setBigDecimal(5, allocation.getOtherAmount());
            stmt.setString(6, allocation.getDescription());
            stmt.setDate(7, allocation.getAllocationDate() != null ? Date.valueOf(allocation.getAllocationDate()) : null);
            stmt.setString(8, allocation.getStatus());
            stmt.setInt(9, allocation.getId());
            stmt.setInt(10, allocation.getUserId());
            
            stmt.executeUpdate();
        }
    }
    
    public void delete(int id, int userId) throws SQLException {
        String sql = "DELETE FROM remittance_allocations WHERE id = ? AND user_id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        }
    }
    
    public RemittanceAllocation findById(int id, int userId) throws SQLException {
        String sql = "SELECT * FROM remittance_allocations WHERE id = ? AND user_id = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            stmt.setInt(2, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToAllocation(rs);
                }
            }
        }
        return null;
    }
    
    public List<RemittanceAllocation> findByUser(int userId) throws SQLException {
        String sql = "SELECT * FROM remittance_allocations WHERE user_id = ? ORDER BY allocation_date DESC, created_at DESC";
        List<RemittanceAllocation> allocations = new ArrayList<>();
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    allocations.add(mapResultSetToAllocation(rs));
                }
            }
        }
        return allocations;
    }
    
    public List<RemittanceAllocation> findByUserAndMonth(int userId, int year, int month) throws SQLException {
        String sql = "SELECT * FROM remittance_allocations WHERE user_id = ? AND YEAR(allocation_date) = ? AND MONTH(allocation_date) = ? ORDER BY allocation_date DESC";
        List<RemittanceAllocation> allocations = new ArrayList<>();
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setInt(2, year);
            stmt.setInt(3, month);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    allocations.add(mapResultSetToAllocation(rs));
                }
            }
        }
        return allocations;
    }
    
    public BigDecimal sumTotalByUserAndMonth(int userId, int year, int month) throws SQLException {
        String sql = "SELECT COALESCE(SUM(total_amount), 0) as total FROM remittance_allocations WHERE user_id = ? AND YEAR(allocation_date) = ? AND MONTH(allocation_date) = ?";
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, userId);
            stmt.setInt(2, year);
            stmt.setInt(3, month);
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("total");
                }
            }
        }
        return BigDecimal.ZERO;
    }
    
    private RemittanceAllocation mapResultSetToAllocation(ResultSet rs) throws SQLException {
        RemittanceAllocation allocation = new RemittanceAllocation();
        allocation.setId(rs.getInt("id"));
        allocation.setUserId(rs.getInt("user_id"));
        allocation.setTotalAmount(rs.getBigDecimal("total_amount"));
        allocation.setRentAmount(rs.getBigDecimal("rent_amount"));
        allocation.setFoodAmount(rs.getBigDecimal("food_amount"));
        allocation.setSavingsAmount(rs.getBigDecimal("savings_amount"));
        allocation.setOtherAmount(rs.getBigDecimal("other_amount"));
        allocation.setDescription(rs.getString("description"));
        
        Date allocationDate = rs.getDate("allocation_date");
        if (allocationDate != null) {
            allocation.setAllocationDate(allocationDate.toLocalDate());
        }
        
        Timestamp createdAt = rs.getTimestamp("created_at");
        if (createdAt != null) {
            allocation.setCreatedAt(createdAt.toLocalDateTime());
        }
        
        allocation.setStatus(rs.getString("status"));
        
        return allocation;
    }
}
