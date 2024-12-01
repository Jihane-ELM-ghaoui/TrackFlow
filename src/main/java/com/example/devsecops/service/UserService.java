package com.example.devsecops.service;

import com.example.devsecops.model.User;

public interface UserService {
    void saveUser();

    void emailVerification();

    User findUser(String email);
}
