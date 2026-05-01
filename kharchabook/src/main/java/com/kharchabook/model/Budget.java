/**
 * Model class representing a monthly budget for a specific category.
 * Stores budget limit, spent amount, and related metadata.
 */
package com.kharchabook.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class Budget implements Serializable {

    private int id;
    private int userId;
    private int categoryId;
    private String categoryName;
    private BigDecimal monthlyLimit;
    private int month;
    private int year;
    private BigDecimal spentAmount;

    // Getters and setters

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

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public BigDecimal getMonthlyLimit() {
        return monthlyLimit;
    }

    public void setMonthlyLimit(BigDecimal monthlyLimit) {
        this.monthlyLimit = monthlyLimit;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public BigDecimal getSpentAmount() {
        return spentAmount;
    }

    public void setSpentAmount(BigDecimal spentAmount) {
        this.spentAmount = spentAmount;
    }

    /**
     * Calculate percentage of budget used.
     * Returns 0 if limit is not set or invalid.
     */
    public double getPercentUsed() {
        if (monthlyLimit == null || monthlyLimit.compareTo(BigDecimal.ZERO) <= 0) {
            return 0;
        }

        BigDecimal sp = spentAmount != null ? spentAmount : BigDecimal.ZERO;

        return sp.multiply(BigDecimal.valueOf(100))
                .divide(monthlyLimit, 2, java.math.RoundingMode.HALF_UP)
                .doubleValue();
    }
}