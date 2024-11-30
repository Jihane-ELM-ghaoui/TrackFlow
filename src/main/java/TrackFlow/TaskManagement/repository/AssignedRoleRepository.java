package TrackFlow.TaskManagement.repository;

import TrackFlow.TaskManagement.model.AssignedRole;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AssignedRoleRepository extends JpaRepository<AssignedRole, Long> {
    List<AssignedRole> findByTaskId(Long taskId);
}
