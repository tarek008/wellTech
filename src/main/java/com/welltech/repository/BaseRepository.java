package com.welltech.repository;

import java.util.List;

/**
 * Base repository interface defining common CRUD operations
 * @param <T> Entity type
 * @param <ID> ID type
 */
public interface BaseRepository<T, ID> {
    
    /**
     * Save an entity to the database
     * @param entity Entity to save
     * @return ID of the saved entity
     */
    ID save(T entity);
    
    /**
     * Find an entity by its ID
     * @param id Entity ID
     * @return Entity if found, null otherwise
     */
    T findById(ID id);
    
    /**
     * Get all entities
     * @return List of all entities
     */
    List<T> findAll();
    
    /**
     * Update an entity
     * @param entity Entity to update
     * @return true if successful, false otherwise
     */
    boolean update(T entity);
    
    /**
     * Delete an entity by its ID
     * @param id Entity ID
     * @return true if successful, false otherwise
     */
    boolean deleteById(ID id);
} 