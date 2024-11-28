package com.example.userKPI.repo;

import com.example.userKPI.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepo extends JpaRepository<Task, Long> {

    @Query("SELECT COUNT(t) FROM Task t WHERE t.assignedUser = :userId")
    int countAllTasksByUserId(String userId);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.assignedUser = :userId AND t.status = 'completed'")
    int countCompletedTasksByUserId(String userId);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.assignedUser = :userId AND t.status != 'completed'")
    int countIncompleteTasksByUserId(String userId);

    @Query("SELECT t.status, COUNT(t) FROM Task t WHERE t.assignedUser = :userId GROUP BY t.status")
    List<Object[]> countTasksByStatus(String userId);
}
