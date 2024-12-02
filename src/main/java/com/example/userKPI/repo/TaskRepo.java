package com.example.userKPI.repo;

import com.example.userKPI.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
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

    //project

    // Query to find task IDs and cycle times for completed tasks in a project
    @Query("SELECT t.taskid, DATEDIFF(t.taskEndDate, t.taskStartDate) " +
            "FROM Task t WHERE t.projectId = :projectId AND t.status = 'COMPLETED'")
    List<Object[]> findTaskIdAndCycleTimeByProjectId(@Param("projectId") Long projectId);

    // Count the total number of tasks in a project
    @Query("SELECT COUNT(t) FROM Task t WHERE t.projectId = :projectId")
    long countTasksByProjectId(@Param("projectId") Long projectId);

    // Count the number of completed tasks that were completed on time
    @Query("SELECT COUNT(t) FROM Task t WHERE t.projectId = :projectId AND t.taskEndDate <= t.taskEstimatedEndDate AND t.status = 'COMPLETED'")
    long countOnTimeTasksByProjectId(@Param("projectId") Long projectId);

    // Count the total number of completed tasks in a project
    @Query("SELECT COUNT(t) FROM Task t WHERE t.projectId = :projectId AND t.status = 'COMPLETED'")
    long countCompletedTasksByProjectId(@Param("projectId") Long projectId);
}

