package com.kharchabook.util;

import java.sql.Connection;
import java.sql.Statement;
import java.util.Scanner;

public class DatabaseSetup {
    public static void main(String[] args) {
        try {
            Connection conn = DBUtil.getConnection();
            Statement stmt = conn.createStatement();
            
            System.out.println("Creating missing database tables...");
            
            // Create fees table
            String createFeesTable = """
                CREATE TABLE IF NOT EXISTS fees (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    user_id INT NOT NULL,
                    fee_name VARCHAR(100) NOT NULL,
                    amount DECIMAL(15,2) NOT NULL,
                    due_date DATE NOT NULL,
                    description TEXT,
                    status VARCHAR(20) DEFAULT 'active',
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                    INDEX idx_user_date (user_id, due_date),
                    INDEX idx_user_status (user_id, status),
                    INDEX idx_due_date (due_date)
                )
                """;
            
            stmt.executeUpdate(createFeesTable);
            System.out.println("✅ Fees table created successfully");
            
            // Create remittance_allocations table
            String createRemittanceTable = """
                CREATE TABLE IF NOT EXISTS remittance_allocations (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    user_id INT NOT NULL,
                    total_amount DECIMAL(15,2) NOT NULL,
                    rent_amount DECIMAL(15,2) DEFAULT 0,
                    food_amount DECIMAL(15,2) DEFAULT 0,
                    savings_amount DECIMAL(15,2) DEFAULT 0,
                    other_amount DECIMAL(15,2) DEFAULT 0,
                    description TEXT,
                    allocation_date DATE NOT NULL,
                    status VARCHAR(20) DEFAULT 'active',
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
                    INDEX idx_user_date (user_id, allocation_date),
                    INDEX idx_user_status (user_id, status)
                )
                """;
            
            stmt.executeUpdate(createRemittanceTable);
            System.out.println("✅ Remittance allocations table created successfully");
            
            // Verify tables exist
            var checkFees = stmt.executeQuery("SHOW TABLES LIKE 'fees'");
            if (checkFees.next()) {
                System.out.println("✅ Fees table verified to exist");
            }
            
            var checkRemittance = stmt.executeQuery("SHOW TABLES LIKE 'remittance_allocations'");
            if (checkRemittance.next()) {
                System.out.println("✅ Remittance allocations table verified to exist");
            }
            
            System.out.println("\n🎉 Database setup completed successfully!");
            System.out.println("You can now restart your application and the fee reminder functionality will work.");
            
            stmt.close();
            conn.close();
            
        } catch (Exception e) {
            System.err.println("❌ Error creating tables: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
