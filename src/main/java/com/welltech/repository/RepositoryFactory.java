package com.welltech.repository;

import com.welltech.repository.impl.ObjectiveRepositoryImpl;

/**
 * Factory class to provide repository instances
 * This helps manage singleton instances and centralizes repository creation
 */
public class RepositoryFactory {
    
    private static RepositoryFactory instance;
    
    // Repository instances (lazy initialized)
    private ObjectiveRepository objectiveRepository;
    
    /**
     * Private constructor to enforce singleton pattern
     */
    private RepositoryFactory() {
    }
    
    /**
     * Get the singleton instance of the factory
     * @return RepositoryFactory instance
     */
    public static synchronized RepositoryFactory getInstance() {
        if (instance == null) {
            instance = new RepositoryFactory();
        }
        return instance;
    }
    
    /**
     * Get the ObjectiveRepository instance
     * @return ObjectiveRepository instance
     */
    public synchronized ObjectiveRepository getObjectiveRepository() {
        if (objectiveRepository == null) {
            objectiveRepository = new ObjectiveRepositoryImpl();
        }
        return objectiveRepository;
    }
    
    /**
     * Reset all repository instances (useful for testing)
     */
    public synchronized void resetRepositories() {
        objectiveRepository = null;
    }
} 