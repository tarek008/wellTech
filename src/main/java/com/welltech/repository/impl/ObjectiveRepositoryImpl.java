package com.welltech.repository.impl;

import com.welltech.dao.ObjectiveDAO;
import com.welltech.model.Objective;
import com.welltech.repository.ObjectiveRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Implementation of the ObjectiveRepository interface
 * Uses ObjectiveDAO to perform the actual database operations
 */
public class ObjectiveRepositoryImpl implements ObjectiveRepository {
    
    private final ObjectiveDAO objectiveDAO;
    
    /**
     * Constructor
     */
    public ObjectiveRepositoryImpl() {
        this.objectiveDAO = new ObjectiveDAO();
    }
    
    /**
     * Constructor with DAO injection for testing
     * @param objectiveDAO ObjectiveDAO instance
     */
    public ObjectiveRepositoryImpl(ObjectiveDAO objectiveDAO) {
        this.objectiveDAO = objectiveDAO;
    }
    
    @Override
    public Integer save(Objective objective) {
        return objectiveDAO.insertObjective(objective);
    }
    
    @Override
    public Objective findById(Integer id) {
        return objectiveDAO.getObjectiveById(id);
    }
    
    @Override
    public List<Objective> findAll() {
        return objectiveDAO.getAllObjectives();
    }
    
    @Override
    public boolean update(Objective objective) {
        return objectiveDAO.updateObjective(objective);
    }
    
    @Override
    public boolean deleteById(Integer id) {
        return objectiveDAO.deleteObjective(id);
    }
    
    @Override
    public List<Objective> findObjectivesWithMinimumPoints(int points) {
        return objectiveDAO.findObjectivesWithMinimumPoints(points);
    }
    
    @Override
    public List<Objective> findByTitle(String title) {
        return objectiveDAO.findByTitle(title);
    }
    
    @Override
    public List<Objective> findByDateRange(LocalDateTime start, LocalDateTime end) {
        return objectiveDAO.findByDateRange(start, end);
    }
    
    @Override
    public List<Objective> findTopObjectives(int limit) {
        return objectiveDAO.findTopObjectives(limit);
    }
} 