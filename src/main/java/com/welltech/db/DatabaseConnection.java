package com.welltech.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Handles database connection for the application
 */
public class DatabaseConnection {

    private static final String URL = "jdbc:mysql://localhost:3306/pi";
    private static final String USER = "root";
    private static final String PASSWORD = "root";
    
    /**
     * Get a connection to the database
     * @return Connection object
     * @throws SQLException if connection fails
     */
    public static Connection getConnection() throws SQLException {
        try {
            // Ensure the driver is loaded
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Create connection
            Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Database connection established");
            return connection;
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found: " + e.getMessage());
            throw new SQLException("Database driver not found", e);
        } catch (SQLException e) {
            System.err.println("Failed to connect to database: " + e.getMessage());
            throw e;
        }
    }
    
    /**
     * Initialize database by creating necessary tables if they don't exist
     */
    public static void initializeDatabase() {
        try (Connection connection = getConnection();
             Statement statement = connection.createStatement()) {
            
            // Create users table (this is the base table that other tables reference)
            String createUsersTable = "CREATE TABLE IF NOT EXISTS users ("
                    + "id INT PRIMARY KEY AUTO_INCREMENT,"
                    + "username VARCHAR(50) NOT NULL UNIQUE,"
                    + "password VARCHAR(255) NOT NULL,"
                    + "full_name VARCHAR(100) NOT NULL,"
                    + "email VARCHAR(100) NOT NULL UNIQUE,"
                    + "phone_number VARCHAR(20),"
                    + "role ENUM('PATIENT', 'PSYCHIATRIST', 'ADMIN') NOT NULL,"
                    + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                    + "updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP"
                    + ")";
            statement.executeUpdate(createUsersTable);
            
            // Create coupon table
            String createCouponTable = "CREATE TABLE IF NOT EXISTS coupon ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "name VARCHAR(100) NOT NULL,"
                    + "discount_percentage INT NOT NULL CHECK (discount_percentage BETWEEN 0 AND 100),"
                    + "code VARCHAR(50) NOT NULL UNIQUE,"
                    + "is_active TINYINT(1) DEFAULT 1,"
                    + "expiration_date DATETIME NOT NULL,"
                    + "usage_count INT DEFAULT 0"
                    + ")";
            statement.executeUpdate(createCouponTable);
            
            // Create consultation_coupon table
            String createConsultationCouponTable = "CREATE TABLE IF NOT EXISTS consultation_coupon ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "coupon_id INT NOT NULL,"
                    + "user_id INT NOT NULL,"
                    + "nbr_consultation INT NOT NULL,"
                    + "status ENUM('pending', 'completed') DEFAULT 'pending',"
                    + "FOREIGN KEY (coupon_id) REFERENCES coupon(id) ON DELETE CASCADE,"
                    + "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE"
                    + ")";
            statement.executeUpdate(createConsultationCouponTable);
            
            // Create objective table
            String createObjectiveTable = "CREATE TABLE IF NOT EXISTS objective ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "title VARCHAR(255) NOT NULL,"
                    + "description TEXT NOT NULL,"
                    + "points_required INT NOT NULL CHECK (points_required >= 0),"
                    + "created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                    + "updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,"
                    + "user_id INT,"
                    + "FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL"
                    + ")";
            statement.executeUpdate(createObjectiveTable);
            
            System.out.println("Database initialized successfully");
        } catch (SQLException e) {
            System.err.println("Failed to initialize database: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 