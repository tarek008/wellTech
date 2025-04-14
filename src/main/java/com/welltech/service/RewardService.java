package com.welltech.service;

import com.welltech.dao.ConsultationCouponDAO;
import com.welltech.dao.CouponDAO;
import com.welltech.model.ConsultationCoupon;
import com.welltech.model.Coupon;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service class that handles the reward system logic
 */
public class RewardService {
    
    private final ConsultationCouponDAO consultationCouponDAO;
    private final CouponDAO couponDAO;
    
    /**
     * Constructor
     */
    public RewardService() {
        this.consultationCouponDAO = new ConsultationCouponDAO();
        this.couponDAO = new CouponDAO();
    }
    
    /**
     * Constructor with DAOs for testing
     */
    public RewardService(ConsultationCouponDAO consultationCouponDAO, CouponDAO couponDAO) {
        this.consultationCouponDAO = consultationCouponDAO;
        this.couponDAO = couponDAO;
    }
    
    /**
     * Create a new objective for a user to earn a coupon
     * @param userId User ID
     * @param couponId Coupon ID
     * @param requiredConsultations Number of consultations required
     * @return ID of the created objective if successful, -1 otherwise
     */
    public int createObjective(int userId, int couponId, int requiredConsultations) {
        // Check if coupon exists
        Coupon coupon = couponDAO.getCouponById(couponId);
        if (coupon == null) {
            System.err.println("Coupon with ID " + couponId + " not found");
            return -1;
        }
        
        // Create new consultation-coupon objective
        ConsultationCoupon objective = new ConsultationCoupon(couponId, userId, requiredConsultations);
        return consultationCouponDAO.insertConsultationCoupon(objective);
    }
    
    /**
     * Create a new coupon
     * @param name Coupon name
     * @param discountPercentage Discount percentage (0-100)
     * @param code Unique coupon code
     * @param expirationDate Expiration date
     * @return ID of the created coupon if successful, -1 otherwise
     */
    public int createCoupon(String name, int discountPercentage, String code, LocalDateTime expirationDate) {
        // Validate input
        if (name == null || name.trim().isEmpty()) {
            System.err.println("Coupon name cannot be empty");
            return -1;
        }
        
        if (discountPercentage < 0 || discountPercentage > 100) {
            System.err.println("Discount percentage must be between 0 and 100");
            return -1;
        }
        
        if (code == null || code.trim().isEmpty()) {
            System.err.println("Coupon code cannot be empty");
            return -1;
        }
        
        // Check if code is unique
        Coupon existingCoupon = couponDAO.getCouponByCode(code);
        if (existingCoupon != null) {
            System.err.println("Coupon code already exists: " + code);
            return -1;
        }
        
        // Create new coupon
        Coupon coupon = new Coupon(name, discountPercentage, code, expirationDate);
        return couponDAO.insertCoupon(coupon);
    }
    
    /**
     * Get all active coupons that a user has earned
     * @param userId User ID
     * @return List of active Coupon objects earned by the user
     */
    public List<Coupon> getUserEarnedCoupons(int userId) {
        List<Coupon> earnedCoupons = new ArrayList<>();
        
        // Get completed objectives for the user
        List<ConsultationCoupon> completedObjectives = consultationCouponDAO.getConsultationCouponsByUser(userId)
                .stream()
                .filter(ConsultationCoupon::isCompleted)
                .collect(Collectors.toList());
        
        // Get the coupons for these objectives
        for (ConsultationCoupon objective : completedObjectives) {
            Coupon coupon = couponDAO.getCouponById(objective.getCouponId());
            if (coupon != null && coupon.isValid()) {
                earnedCoupons.add(coupon);
            }
        }
        
        return earnedCoupons;
    }
    
    /**
     * Get all pending objectives for a user
     * @param userId User ID
     * @return List of pending ConsultationCoupon objectives
     */
    public List<ConsultationCoupon> getUserPendingObjectives(int userId) {
        return consultationCouponDAO.getPendingConsultationCouponsByUser(userId);
    }
    
    /**
     * Check if a user has completed any objectives based on their consultation count
     * @param userId User ID
     * @param consultationsCompleted Number of consultations completed
     * @return List of newly completed objective IDs
     */
    public List<Integer> checkCompletedObjectives(int userId, int consultationsCompleted) {
        return consultationCouponDAO.checkCompletedObjectives(userId, consultationsCompleted);
    }
    
    /**
     * Apply a coupon (increment usage count)
     * @param couponId Coupon ID
     * @return true if successful, false otherwise
     */
    public boolean applyCoupon(int couponId) {
        // Check if coupon exists and is valid
        Coupon coupon = couponDAO.getCouponById(couponId);
        if (coupon == null) {
            System.err.println("Coupon with ID " + couponId + " not found");
            return false;
        }
        
        if (!coupon.isValid()) {
            System.err.println("Coupon is not valid (expired or inactive): " + coupon.getCode());
            return false;
        }
        
        // Increment usage count
        return couponDAO.incrementUsageCount(couponId);
    }
    
    /**
     * Apply a coupon by code
     * @param code Coupon code
     * @return true if successful, false otherwise
     */
    public boolean applyCouponByCode(String code) {
        // Check if coupon exists and is valid
        Coupon coupon = couponDAO.getCouponByCode(code);
        if (coupon == null) {
            System.err.println("Coupon with code " + code + " not found");
            return false;
        }
        
        if (!coupon.isValid()) {
            System.err.println("Coupon is not valid (expired or inactive): " + coupon.getCode());
            return false;
        }
        
        // Increment usage count
        return couponDAO.incrementUsageCount(coupon.getId());
    }
} 