package TrackFlow.TaskManagement.repository;

import TrackFlow.TaskManagement.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TaskRepository extends JpaRepository<Task, Long> {

    @Query("SELECT COUNT(t) FROM Task t WHERE :userId MEMBER OF t.assignedUsers")
    int countAllTasksByUserId(String userId);

    @Query("SELECT COUNT(t) FROM Task t WHERE :userId MEMBER OF t.assignedUsers AND t.status = 'completed'")
    int countCompletedTasksByUserId(String userId);

    @Query("SELECT COUNT(t) FROM Task t WHERE :userId MEMBER OF t.assignedUsers AND t.status != 'completed'")
    int countIncompleteTasksByUserId(String userId);

    @Query("SELECT CAST(t.status AS string), COUNT(t) FROM Task t WHERE :userId MEMBER OF t.assignedUsers GROUP BY t.status")
    List<Object[]> countTasksByStatus(String userId);

    @Query("SELECT t.taskId, DATEDIFF(t.taskEndDate, t.taskStartDate) " +
            "FROM Task t WHERE t.projectId = :projectId AND t.status = 'COMPLETED'")
    List<Object[]> findTaskIdAndCycleTimeByProjectId(Long projectId);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.projectId = :projectId")
    long countTasksByProjectId(Long projectId);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.projectId = :projectId AND t.taskEndDate <= t.taskEstimatedEndDate AND t.status = 'COMPLETED'")
    long countOnTimeTasksByProjectId( Long projectId);

    @Query("SELECT COUNT(t) FROM Task t WHERE t.projectId = :projectId AND t.status = 'COMPLETED'")
    long countCompletedTasksByProjectId( Long projectId);

    List<Task> findByProjectId(Long projectId);

    List<Task> findTop5ByAssignedUsersContainingOrderByCreatedAtDesc(String userId);
}