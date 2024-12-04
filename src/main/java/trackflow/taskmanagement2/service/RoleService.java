package trackflow.taskmanagement2.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import trackflow.taskmanagement2.bean.Role;
import trackflow.taskmanagement2.dao.RoleRepository;

import jakarta.persistence.EntityNotFoundException;
import java.util.List;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    public Role createRole(String RoleName) {
        Role role = new Role();
        role.setName(RoleName);
        return roleRepository.save(role);
    }
    public Role getRoleById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Role not found"));
    }
    public Role getRoleByName(String roleName) {
        return roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found"));
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }
}

