package com.kharchabook.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class Fee implements Serializable {
    private int id;
    private int userId;
    private String feeName;
    private BigDecimal amount;
    private LocalDate dueDate;
    private String description;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long daysUntilDue;
    private boolean isOverdue;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getFeeName() {
        return feeName;
    }

    public void setFeeName(String feeName) {
        this.feeName = feeName;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Long getDaysUntilDue() {
        return daysUntilDue;
    }

    public void setDaysUntilDue(Long daysUntilDue) {
        this.daysUntilDue = daysUntilDue;
    }

    public boolean isOverdue() {
        return isOverdue;
    }

    public void setOverdue(boolean overdue) {
        isOverdue = overdue;
    }

    public boolean isDueSoon(int daysThreshold) {
        if (daysUntilDue == null) return false;
        return daysUntilDue >= 0 && daysUntilDue <= daysThreshold;
    }

    public boolean isActive() {
        return "active".equalsIgnoreCase(status);
    }

    public boolean isPaid() {
        return "paid".equalsIgnoreCase(status);
    }
}
