package trackflow.taskmanagement2.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import trackflow.taskmanagement2.bean.Role;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);

}

