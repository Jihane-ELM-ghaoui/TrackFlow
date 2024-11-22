package com.example.devsecops.controller;



import com.example.devsecops.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@CrossOrigin("http://localhost:3000")
public class Controller {


    @Autowired
    private UserService userService;

    @GetMapping("/api/protected")
    @PreAuthorize("")
    public void saveUser() {
        userService.emailVerification();
        userService.saveUser();

    }
}
