package trackflow.taskmanagement2.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import trackflow.taskmanagement2.bean.Project;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository extends JpaRepository<Project, Long> {
    Optional<Project> findByName(String name);
    List<Project> findByLeaderid(String leaderId);
}
