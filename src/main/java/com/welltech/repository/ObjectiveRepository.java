package com.welltech.repository;

import com.welltech.model.Objective;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Objective-related database operations
 */
public interface ObjectiveRepository extends BaseRepository<Objective, Integer> {
    
    /**
     * Find objectives with minimum points requirement
     * @param points Minimum points required
     * @return List of Objective objects with pointsRequired >= points, ordered by pointsRequired DESC
     */
    List<Objective> findObjectivesWithMinimumPoints(int points);
    
    /**
     * Find objectives by title (case-insensitive)
     * @param title Title to search for
     * @return List of Objective objects with title LIKE %title%, ordered by title ASC
     */
    List<Objective> findByTitle(String title);
    
    /**
     * Find objectives created between two dates
     * @param start Start date
     * @param end End date
     * @return List of Objective objects created between start and end, ordered by createdAt DESC
     */
    List<Objective> findByDateRange(LocalDateTime start, LocalDateTime end);
    
    /**
     * Find top N objectives with highest points requirement
     * @param limit Maximum number of objectives to return
     * @return List of top N Objective objects by pointsRequired, sorted DESC
     */
    List<Objective> findTopObjectives(int limit);
} 