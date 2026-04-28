package com.kharchabook.model;

import java.io.Serializable;
import java.time.LocalDateTime;

public class FinancialTip implements Serializable {
    private int id;
    private String title;
    private String content;
    private String category;
    private int postedBy;
    private LocalDateTime postedDate;
    private long wishlistCount;
    private boolean wishlistedByCurrentUser;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getPostedBy() {
        return postedBy;
    }

    public void setPostedBy(int postedBy) {
        this.postedBy = postedBy;
    }

    public LocalDateTime getPostedDate() {
        return postedDate;
    }

    public void setPostedDate(LocalDateTime postedDate) {
        this.postedDate = postedDate;
    }

    public long getWishlistCount() {
        return wishlistCount;
    }

    public void setWishlistCount(long wishlistCount) {
        this.wishlistCount = wishlistCount;
    }

    public boolean isWishlistedByCurrentUser() {
        return wishlistedByCurrentUser;
    }

    public void setWishlistedByCurrentUser(boolean wishlistedByCurrentUser) {
        this.wishlistedByCurrentUser = wishlistedByCurrentUser;
    }
}
