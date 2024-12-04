package trackflow.taskmanagement2.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import trackflow.taskmanagement2.bean.Role;
import trackflow.taskmanagement2.service.RoleService;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
public class RoleController {
    @Autowired
    private RoleService roleService;

    @PostMapping
    public Role createRole(@RequestParam String RoleName) {
        return roleService.createRole(RoleName);
    }

}
