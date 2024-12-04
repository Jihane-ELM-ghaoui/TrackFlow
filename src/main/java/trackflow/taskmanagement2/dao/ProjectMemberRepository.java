package trackflow.taskmanagement2.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import trackflow.taskmanagement2.bean.Project;
import trackflow.taskmanagement2.bean.ProjectMember;
import trackflow.taskmanagement2.bean.Role;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface ProjectMemberRepository extends JpaRepository<ProjectMember, Long> {
    Optional<ProjectMember> findByProjectNameAndEmail(String projectName, String email);
    Optional<ProjectMember> findByInvitationToken(String token);

    List<ProjectMember> findByProjectAndStatus(Project project, String status);
}

