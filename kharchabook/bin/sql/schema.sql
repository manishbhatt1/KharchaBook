-- KharchaBook — MySQL 8.x
-- Run: mysql -u root -p < schema.sql

CREATE DATABASE IF NOT EXISTS kharchabook CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE kharchabook;

DROP TABLE IF EXISTS bill_reminders;
DROP TABLE IF EXISTS wishlist;
DROP TABLE IF EXISTS fees;
DROP TABLE IF EXISTS remittance_allocations;
DROP TABLE IF EXISTS transactions;
DROP TABLE IF EXISTS budgets;
DROP TABLE IF EXISTS savings_goals;
DROP TABLE IF EXISTS financial_tips;
DROP TABLE IF EXISTS categories;
DROP TABLE IF EXISTS remember_tokens;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(120) NOT NULL,
    email VARCHAR(120) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    password VARCHAR(64) NOT NULL COMMENT 'SHA-256 hex',
    role ENUM('ADMIN','USER') NOT NULL DEFAULT 'USER',
    status ENUM('PENDING','ACTIVE','BLOCKED') NOT NULL DEFAULT 'PENDING',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_email (email),
    UNIQUE KEY uk_phone (phone)
) ENGINE=InnoDB;

CREATE TABLE remember_tokens (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    selector VARCHAR(24) NOT NULL,
    token_hash VARCHAR(64) NOT NULL COMMENT 'SHA-256 hex of validator',
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    UNIQUE KEY uk_selector (selector),
    INDEX idx_user_expires (user_id, expires_at)
) ENGINE=InnoDB;

CREATE TABLE categories (
    id INT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    type ENUM('income','expense') NOT NULL,
    icon VARCHAR(50) DEFAULT NULL,
    created_by INT DEFAULT NULL,
    FOREIGN KEY (created_by) REFERENCES users(id) ON DELETE SET NULL
) ENGINE=InnoDB;

CREATE TABLE transactions (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    category_id INT NOT NULL,
    type ENUM('income','expense') NOT NULL,
    amount DECIMAL(14,2) NOT NULL,
    description VARCHAR(500) DEFAULT NULL,
    transaction_date DATE NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE RESTRICT,
    INDEX idx_user_date (user_id, transaction_date),
    INDEX idx_user_type (user_id, type)
) ENGINE=InnoDB;

CREATE TABLE budgets (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    category_id INT NOT NULL,
    monthly_limit DECIMAL(14,2) NOT NULL,
    month TINYINT NOT NULL CHECK (month BETWEEN 1 AND 12),
    year SMALLINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (category_id) REFERENCES categories(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_cat_month (user_id, category_id, month, year)
) ENGINE=InnoDB;

CREATE TABLE savings_goals (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    title VARCHAR(200) NOT NULL,
    target_amount DECIMAL(14,2) NOT NULL,
    saved_amount DECIMAL(14,2) NOT NULL DEFAULT 0,
    deadline DATE DEFAULT NULL,
    status ENUM('active','completed','cancelled') NOT NULL DEFAULT 'active',
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE financial_tips (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    content TEXT NOT NULL,
    category VARCHAR(50) NOT NULL,
    posted_by INT NOT NULL,
    posted_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (posted_by) REFERENCES users(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE bill_reminders (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    bill_name VARCHAR(150) NOT NULL,
    amount DECIMAL(14,2) NOT NULL,
    due_date DATE NOT NULL,
    due_day TINYINT NOT NULL CHECK (due_day BETWEEN 1 AND 31),
    frequency ENUM('monthly','yearly') NOT NULL DEFAULT 'monthly',
    notes VARCHAR(500) DEFAULT NULL,
    status ENUM('active','paused') NOT NULL DEFAULT 'active',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    INDEX idx_bill_user_status (user_id, status),
    INDEX idx_bill_user_due_day (user_id, due_day)
) ENGINE=InnoDB;

CREATE TABLE fees (
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
    INDEX idx_fee_user_date (user_id, due_date),
    INDEX idx_fee_user_status (user_id, status),
    INDEX idx_fee_due_date (due_date)
) ENGINE=InnoDB;

CREATE TABLE remittance_allocations (
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
    INDEX idx_remittance_user_date (user_id, allocation_date),
    INDEX idx_remittance_user_status (user_id, status)
) ENGINE=InnoDB;

CREATE TABLE wishlist (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT NOT NULL,
    tip_id INT NOT NULL,
    saved_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (tip_id) REFERENCES financial_tips(id) ON DELETE CASCADE,
    UNIQUE KEY uk_user_tip (user_id, tip_id)
) ENGINE=InnoDB;

-- Default admin: admin@kharchabook.com / admin123  (SHA-256 hex)
INSERT INTO users (full_name, email, phone, password, role, status) VALUES
('System Admin', 'admin@kharchabook.com', '9800000000', '240be518fabd2724ddb6f04eeb1da5967448d7e831c08c8fa822809f74c720a9', 'ADMIN', 'ACTIVE');

SET @admin_id = LAST_INSERT_ID();

-- Nepal-specific default categories (ids auto)
INSERT INTO categories (name, type, icon, created_by) VALUES
('Salary / Thalaba', 'income', 'salary', @admin_id),
('Pocket Money', 'income', 'pocket', @admin_id),
('Remittance Received', 'income', 'remit', @admin_id),
('Freelance / Part-time', 'income', 'work', @admin_id),
('Business Income', 'income', 'biz', @admin_id),
('Scholarship', 'income', 'edu', @admin_id),
('Food & Groceries / Khaana Kharcha', 'expense', 'food', @admin_id),
('Transport / Yatayat', 'expense', 'transport', @admin_id),
('Rent / Bhada', 'expense', 'rent', @admin_id),
('Bills & Utilities / Bijuli, Paani', 'expense', 'bills', @admin_id),
('Education / Shiksha', 'expense', 'school', @admin_id),
('Entertainment / Manoranjan', 'expense', 'fun', @admin_id),
('Health / Swasthya', 'expense', 'health', @admin_id),
('Dashain / Tihar Expenses', 'expense', 'festival', @admin_id),
('Chanda / Donation', 'expense', 'donation', @admin_id),
('Dhikuti Savings', 'expense', 'dhikuti', @admin_id),
('Other / Anya', 'expense', 'other', @admin_id);

INSERT INTO financial_tips (title, content, category, posted_by) VALUES
('Build a simple monthly budget', 'List your fixed costs and variable costs. Allocate amounts before the month starts and review weekly.', 'Budgeting', @admin_id),
('Emergency fund first', 'Aim for at least one month of essential expenses in a separate savings account.', 'Saving', @admin_id),
('Track remittance carefully', 'When money arrives from abroad, split it: essentials, savings, and discretionary spending.', 'General', @admin_id);
