package com.welltech.example;

import com.welltech.model.Objective;
import com.welltech.repository.ObjectiveRepository;
import com.welltech.repository.RepositoryFactory;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Example class demonstrating how to use the ObjectiveRepository
 */
public class ObjectiveExample {
    
    /**
     * Demonstrate various repository operations
     */
    public static void demonstrateObjectiveRepository() {
        // Get repository instance
        ObjectiveRepository repository = RepositoryFactory.getInstance().getObjectiveRepository();
        
        // Example: Create a new objective
        Objective newObjective = new Objective(
                "Complete Daily Exercises",
                "Perform recommended daily exercises for mental health improvement",
                50,
                null // Not associated with a specific user
        );
        
        Integer objectiveId = repository.save(newObjective);
        System.out.println("Created new objective with ID: " + objectiveId);
        
        // Example: Find objectives with minimum points
        List<Objective> challengingObjectives = repository.findObjectivesWithMinimumPoints(30);
        System.out.println("\nChallenging objectives (30+ points):");
        for (Objective obj : challengingObjectives) {
            System.out.println("- " + obj.getTitle() + " (" + obj.getPointsRequired() + " points)");
        }
        
        // Example: Search by title
        List<Objective> exerciseObjectives = repository.findByTitle("exercise");
        System.out.println("\nObjectives related to exercises:");
        for (Objective obj : exerciseObjectives) {
            System.out.println("- " + obj.getTitle());
        }
        
        // Example: Find by date range (last 7 days)
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime oneWeekAgo = now.minusDays(7);
        List<Objective> recentObjectives = repository.findByDateRange(oneWeekAgo, now);
        System.out.println("\nRecently created objectives (last 7 days):");
        for (Objective obj : recentObjectives) {
            System.out.println("- " + obj.getTitle() + " (Created: " + obj.getCreatedAt() + ")");
        }
        
        // Example: Get top objectives
        List<Objective> topObjectives = repository.findTopObjectives(3);
        System.out.println("\nTop 3 objectives by points:");
        for (Objective obj : topObjectives) {
            System.out.println("- " + obj.getTitle() + " (" + obj.getPointsRequired() + " points)");
        }
    }
    
    /**
     * Main method for standalone testing
     */
    public static void main(String[] args) {
        demonstrateObjectiveRepository();
    }
} 