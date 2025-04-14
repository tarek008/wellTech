package com.welltech.model;

/**
 * ConsultationCoupon model class representing the relationship between users, coupons, and consultation progress
 */
public class ConsultationCoupon {
    private int id;
    private int couponId;
    private int userId;
    private int nbrConsultation;
    private Status status;
    
    // Enum for status
    public enum Status {
        PENDING,
        COMPLETED
    }
    
    // Default constructor
    public ConsultationCoupon() {
        this.status = Status.PENDING;
    }
    
    // Constructor without ID (for creating new consultation-coupon relationships)
    public ConsultationCoupon(int couponId, int userId, int nbrConsultation) {
        this.couponId = couponId;
        this.userId = userId;
        this.nbrConsultation = nbrConsultation;
        this.status = Status.PENDING;
    }
    
    // Full constructor
    public ConsultationCoupon(int id, int couponId, int userId, int nbrConsultation, Status status) {
        this.id = id;
        this.couponId = couponId;
        this.userId = userId;
        this.nbrConsultation = nbrConsultation;
        this.status = status;
    }
    
    // Getters and Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getCouponId() {
        return couponId;
    }
    
    public void setCouponId(int couponId) {
        this.couponId = couponId;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public int getNbrConsultation() {
        return nbrConsultation;
    }
    
    public void setNbrConsultation(int nbrConsultation) {
        this.nbrConsultation = nbrConsultation;
    }
    
    public Status getStatus() {
        return status;
    }
    
    public void setStatus(Status status) {
        this.status = status;
    }
    
    /**
     * Check if the objective is completed
     * @return true if status is COMPLETED, false otherwise
     */
    public boolean isCompleted() {
        return status == Status.COMPLETED;
    }
    
    /**
     * Mark the objective as completed
     */
    public void markAsCompleted() {
        this.status = Status.COMPLETED;
    }
    
    @Override
    public String toString() {
        return "ConsultationCoupon{" +
                "id=" + id +
                ", couponId=" + couponId +
                ", userId=" + userId +
                ", nbrConsultation=" + nbrConsultation +
                ", status=" + status +
                '}';
    }
} 