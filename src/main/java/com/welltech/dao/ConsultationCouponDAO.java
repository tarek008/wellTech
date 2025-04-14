package com.welltech.dao;

import com.welltech.db.DatabaseConnection;
import com.welltech.model.ConsultationCoupon;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for ConsultationCoupon-related database operations
 */
public class ConsultationCouponDAO {
    
    /**
     * Insert a new consultation-coupon relationship into the database
     * @param consultationCoupon ConsultationCoupon object to insert
     * @return Generated ID if successful, -1 otherwise
     */
    public int insertConsultationCoupon(ConsultationCoupon consultationCoupon) {
        String sql = "INSERT INTO consultation_coupon (coupon_id, user_id, nbr_consultation, status) VALUES (?, ?, ?, ?)";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            statement.setInt(1, consultationCoupon.getCouponId());
            statement.setInt(2, consultationCoupon.getUserId());
            statement.setInt(3, consultationCoupon.getNbrConsultation());
            statement.setString(4, consultationCoupon.getStatus().toString().toLowerCase());
            
            int affectedRows = statement.executeUpdate();
            
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = statement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getInt(1);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("Error inserting consultation-coupon: " + e.getMessage());
            e.printStackTrace();
        }
        
        return -1;
    }
    
    /**
     * Get a consultation-coupon relationship by its ID
     * @param id ConsultationCoupon ID
     * @return ConsultationCoupon object if found, null otherwise
     */
    public ConsultationCoupon getConsultationCouponById(int id) {
        String sql = "SELECT * FROM consultation_coupon WHERE id = ?";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, id);
            ResultSet resultSet = statement.executeQuery();
            
            if (resultSet.next()) {
                return extractConsultationCouponFromResultSet(resultSet);
            }
        } catch (SQLException e) {
            System.err.println("Error getting consultation-coupon by ID: " + e.getMessage());
            e.printStackTrace();
        }
        
        return null;
    }
    
    /**
     * Get all consultation-coupon relationships for a specific user
     * @param userId User ID
     * @return List of ConsultationCoupon objects
     */
    public List<ConsultationCoupon> getConsultationCouponsByUser(int userId) {
        List<ConsultationCoupon> consultationCoupons = new ArrayList<>();
        String sql = "SELECT * FROM consultation_coupon WHERE user_id = ?";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                consultationCoupons.add(extractConsultationCouponFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            System.err.println("Error getting consultation-coupons by user: " + e.getMessage());
            e.printStackTrace();
        }
        
        return consultationCoupons;
    }
    
    /**
     * Get pending consultation-coupon relationships for a specific user
     * @param userId User ID
     * @return List of pending ConsultationCoupon objects
     */
    public List<ConsultationCoupon> getPendingConsultationCouponsByUser(int userId) {
        List<ConsultationCoupon> consultationCoupons = new ArrayList<>();
        String sql = "SELECT * FROM consultation_coupon WHERE user_id = ? AND status = 'pending'";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, userId);
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                consultationCoupons.add(extractConsultationCouponFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            System.err.println("Error getting pending consultation-coupons by user: " + e.getMessage());
            e.printStackTrace();
        }
        
        return consultationCoupons;
    }
    
    /**
     * Get all consultation-coupon relationships
     * @return List of ConsultationCoupon objects
     */
    public List<ConsultationCoupon> getAllConsultationCoupons() {
        List<ConsultationCoupon> consultationCoupons = new ArrayList<>();
        String sql = "SELECT * FROM consultation_coupon";
        
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {
            
            while (resultSet.next()) {
                consultationCoupons.add(extractConsultationCouponFromResultSet(resultSet));
            }
        } catch (SQLException e) {
            System.err.println("Error getting all consultation-coupons: " + e.getMessage());
            e.printStackTrace();
        }
        
        return consultationCoupons;
    }
    
    /**
     * Update a consultation-coupon relationship in the database
     * @param consultationCoupon ConsultationCoupon object to update
     * @return true if successful, false otherwise
     */
    public boolean updateConsultationCoupon(ConsultationCoupon consultationCoupon) {
        String sql = "UPDATE consultation_coupon SET coupon_id = ?, user_id = ?, nbr_consultation = ?, status = ? WHERE id = ?";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, consultationCoupon.getCouponId());
            statement.setInt(2, consultationCoupon.getUserId());
            statement.setInt(3, consultationCoupon.getNbrConsultation());
            statement.setString(4, consultationCoupon.getStatus().toString().toLowerCase());
            statement.setInt(5, consultationCoupon.getId());
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error updating consultation-coupon: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Mark a consultation-coupon relationship as completed
     * @param id ConsultationCoupon ID
     * @return true if successful, false otherwise
     */
    public boolean markAsCompleted(int id) {
        String sql = "UPDATE consultation_coupon SET status = 'completed' WHERE id = ?";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, id);
            
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error marking consultation-coupon as completed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Delete a consultation-coupon relationship from the database
     * @param id ID of the ConsultationCoupon to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteConsultationCoupon(int id) {
        String sql = "DELETE FROM consultation_coupon WHERE id = ?";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, id);
            int rowsAffected = statement.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            System.err.println("Error deleting consultation-coupon: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Check if a user has met the consultation target for any of their objectives
     * @param userId User ID
     * @param consultationsCompleted Number of consultations the user has completed
     * @return List of ConsultationCoupon IDs that have been fulfilled
     */
    public List<Integer> checkCompletedObjectives(int userId, int consultationsCompleted) {
        List<Integer> completedObjectiveIds = new ArrayList<>();
        String sql = "SELECT id FROM consultation_coupon WHERE user_id = ? AND status = 'pending' AND nbr_consultation <= ?";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {
            
            statement.setInt(1, userId);
            statement.setInt(2, consultationsCompleted);
            
            ResultSet resultSet = statement.executeQuery();
            
            while (resultSet.next()) {
                completedObjectiveIds.add(resultSet.getInt("id"));
            }
            
            // Automatically mark these as completed
            for (Integer objectiveId : completedObjectiveIds) {
                markAsCompleted(objectiveId);
            }
            
        } catch (SQLException e) {
            System.err.println("Error checking completed objectives: " + e.getMessage());
            e.printStackTrace();
        }
        
        return completedObjectiveIds;
    }
    
    /**
     * Extract consultation-coupon data from a ResultSet
     * @param rs ResultSet containing consultation-coupon data
     * @return ConsultationCoupon object
     * @throws SQLException if a database error occurs
     */
    private ConsultationCoupon extractConsultationCouponFromResultSet(ResultSet rs) throws SQLException {
        ConsultationCoupon consultationCoupon = new ConsultationCoupon();
        consultationCoupon.setId(rs.getInt("id"));
        consultationCoupon.setCouponId(rs.getInt("coupon_id"));
        consultationCoupon.setUserId(rs.getInt("user_id"));
        consultationCoupon.setNbrConsultation(rs.getInt("nbr_consultation"));
        
        String statusStr = rs.getString("status");
        if (statusStr.equalsIgnoreCase("completed")) {
            consultationCoupon.setStatus(ConsultationCoupon.Status.COMPLETED);
        } else {
            consultationCoupon.setStatus(ConsultationCoupon.Status.PENDING);
        }
        
        return consultationCoupon;
    }
} 