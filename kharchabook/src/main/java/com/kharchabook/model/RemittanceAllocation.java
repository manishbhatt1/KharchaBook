/**
 * Model class representing remittance allocation.
 * Stores how incoming money is distributed across categories like rent, food, savings, etc.
 */
package com.kharchabook.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class RemittanceAllocation implements Serializable {

    private int id;
    private int userId;
    private BigDecimal totalAmount;
    private BigDecimal rentAmount;
    private BigDecimal foodAmount;
    private BigDecimal savingsAmount;
    private BigDecimal otherAmount;
    private String description;
    private LocalDate allocationDate;
    private LocalDateTime createdAt;
    private String status;

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

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public BigDecimal getRentAmount() {
        return rentAmount;
    }

    public void setRentAmount(BigDecimal rentAmount) {
        this.rentAmount = rentAmount;
    }

    public BigDecimal getFoodAmount() {
        return foodAmount;
    }

    public void setFoodAmount(BigDecimal foodAmount) {
        this.foodAmount = foodAmount;
    }

    public BigDecimal getSavingsAmount() {
        return savingsAmount;
    }

    public void setSavingsAmount(BigDecimal savingsAmount) {
        this.savingsAmount = savingsAmount;
    }

    public BigDecimal getOtherAmount() {
        return otherAmount;
    }

    public void setOtherAmount(BigDecimal otherAmount) {
        this.otherAmount = otherAmount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getAllocationDate() {
        return allocationDate;
    }

    public void setAllocationDate(LocalDate allocationDate) {
        this.allocationDate = allocationDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Calculates total allocated amount across all categories.
     */
    public BigDecimal getAllocatedTotal() {
        BigDecimal total = BigDecimal.ZERO;

        if (rentAmount != null) total = total.add(rentAmount);
        if (foodAmount != null) total = total.add(foodAmount);
        if (savingsAmount != null) total = total.add(savingsAmount);
        if (otherAmount != null) total = total.add(otherAmount);

        return total;
    }

    /**
     * Returns remaining unallocated amount.
     */
    public BigDecimal getUnallocatedAmount() {
        if (totalAmount == null) return BigDecimal.ZERO;
        return totalAmount.subtract(getAllocatedTotal());
    }

    /**
     * Checks if full amount has been allocated.
     */
    public boolean isFullyAllocated() {
        return getUnallocatedAmount().compareTo(BigDecimal.ZERO) == 0;
    }
}