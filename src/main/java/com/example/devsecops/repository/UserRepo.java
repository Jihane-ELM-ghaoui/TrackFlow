package com.example.devsecops.repository;



import com.example.devsecops.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<User, Integer> {


    boolean existsByEmail(String email);

    boolean existsByUserId(String userId);

    User findUserByEmail(String email);
}
