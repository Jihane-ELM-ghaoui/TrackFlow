package com.example.devsecops.service.impl;



import com.example.devsecops.kafkaConfig.KafkaProducer;
import com.example.devsecops.model.User;
import com.example.devsecops.repository.UserRepo;
import com.example.devsecops.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;


import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.CreateBucketResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;

import java.util.Map;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    public UserRepo userRepo;

    @Autowired
    public KafkaProducer kafkaProducer;

    @Autowired
    private final S3Client s3Client;

    public UserServiceImpl(S3Client s3Client) {
        this.s3Client = s3Client;
    }



    @Override
    public void saveUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth.getName();
        System.out.println("Authenticated User: " + userId);

        JwtAuthenticationToken jwtToken = (JwtAuthenticationToken) auth;
        String email = jwtToken.getToken().getClaim("email");
        System.out.println("Email: " + email);

        String role = auth.getAuthorities().toString();
        System.out.println("role: " + role);

        Jwt jwt = jwtToken.getToken();
        Map<String, Object> claims = jwt.getClaims();
        Map<String, Object> userMetadata = (Map<String, Object>) claims.get("https://demo.app.com/user_metadata");

        String fullName = (String) userMetadata.get("Full_Name");
        System.out.println("Full Name: " + fullName);



        if (userRepo.existsByEmail(email) || userRepo.existsByUserId(userId)) {
            System.out.println("User with this email or userId already exists.");

        } else {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setUserId(userId);
            newUser.setFull_Name(fullName);

            userRepo.save(newUser);
            System.out.println("New user saved to the database.");

            emailVerification();

            createS3Bucket(userId);
            kafkaProducer.sendBucketCreateMessage(userId);

        }
    }

    @Override
    public void emailVerification(){

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        JwtAuthenticationToken jwtToken = (JwtAuthenticationToken) auth;
        boolean email_verified = jwtToken.getToken().getClaim("email_verified");
        System.out.println("email_verified: " + email_verified);

        String userId = auth.getName();

        if (!email_verified){
            kafkaProducer.sendEmailVerificationMessage(userId);
        }

    }

    private void createS3Bucket(String userId) {
        try {
            String bucketName = userId.split("\\|")[1];
            System.out.println("Bucket Name: " + bucketName);

            CreateBucketRequest bucketRequest = CreateBucketRequest.builder()
                    .bucket(bucketName)
                    .build();

            CreateBucketResponse response = s3Client.createBucket(bucketRequest);
            System.out.println("S3 Bucket created successfully: " + response.location());
        } catch (S3Exception e) {
            System.err.println("Failed to create S3 bucket: " + e.awsErrorDetails().errorMessage());
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Invalid userId format. Bucket creation skipped.");
        }
    }


    @Override
        public User findUser(String email){

        if (userRepo.existsByEmail(email)) {

            User user = userRepo.findUserByEmail(email);

            return user;

        } else {
            System.out.println("User with this email don't exist.");
        }

        return null;
    }



}
