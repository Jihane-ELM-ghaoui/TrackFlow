package com.example.devsecops.controller;



import com.example.devsecops.model.User;
import com.example.devsecops.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("http://localhost:3000")
@RequestMapping("/api/protected")
public class Controller {


    @Autowired
    private UserService userService;

    @GetMapping("")
    @PreAuthorize("")
    public void saveUser() {
        userService.saveUser();
    }


    @GetMapping("/search")
    @PreAuthorize("")
    public User findUser(@RequestParam String email) {
        return userService.findUser(email);
    }

}
