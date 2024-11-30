package TrackFlow.TaskManagement.repository;

import TrackFlow.TaskManagement.model.AssignedUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AssignedUserRepository extends JpaRepository<AssignedUser, Long> {
    List<AssignedUser> findByTaskId(Long taskId);
}