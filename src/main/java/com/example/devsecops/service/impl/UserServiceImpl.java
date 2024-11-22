package com.example.devsecops.service.impl;



import com.example.devsecops.kafkaConfig.KafkaProducer;
import com.example.devsecops.model.User;
import com.example.devsecops.repository.UserRepo;
import com.example.devsecops.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.CreateBucketResponse;
import software.amazon.awssdk.services.s3.model.S3Exception;


@Service
public class UserServiceImpl implements UserService {

    @Autowired
    public UserRepo userRepo;

    @Autowired
    private KafkaProducer kafkaProducer;

    private final S3Client s3Client;

    public UserServiceImpl(S3Client s3Client) {
        this.s3Client = s3Client;
    }


    @Override
    public void saveUser(){
        // Retrieve the current authentication object
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        // Extract the userId from the authentication object
        String userId = auth.getName();
        System.out.println("Authenticated User: " + userId);

        // Cast the authentication object to JwtAuthenticationToken to access token claims
        JwtAuthenticationToken jwtToken = (JwtAuthenticationToken) auth;
        // Extract the email from the JWT token
        String email = jwtToken.getToken().getClaim("email");
        System.out.println("Email: " + email);

        // Check if a user with the same email or userId already exists in the database
        if (userRepo.existsByEmail(email) || userRepo.existsByUserId(userId)) {
            System.out.println("User with this email or userId already exists.");

        } else {
            User newUser = new User();
            newUser.setEmail(email);
            newUser.setUserId(userId);

            // Save the new user to the database
            userRepo.save(newUser);
            System.out.println("New user saved to the database.");

            // Create an S3 bucket with the userId
            createS3Bucket(userId);
            kafkaProducer.sendBucketCreateMessage(userId);
        }
    }

    @Override
    public void emailVerification(){

        // Retrieve the current authentication object
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        JwtAuthenticationToken jwtToken = (JwtAuthenticationToken) auth;
        // Extract the email from the JWT token
        boolean email_verified = jwtToken.getToken().getClaim("email_verified");
        System.out.println("email_verified: " + email_verified);

        // Extract the userId from the authentication object
        String userId = auth.getName();

        if (!email_verified){
            kafkaProducer.sendEmailVerificationMessage(userId);
        }

    }

    private void createS3Bucket(String userId) {
        try {
            // Extract the numeric portion from the userId
            String bucketName = userId.split("\\|")[1];
            System.out.println("Bucket Name: " + bucketName);

            // Construct a bucket creation request
            CreateBucketRequest bucketRequest = CreateBucketRequest.builder()
                    .bucket(bucketName)
                    .build();

            // Create the bucket
            CreateBucketResponse response = s3Client.createBucket(bucketRequest);
            System.out.println("S3 Bucket created successfully: " + response.location());
        } catch (S3Exception e) {
            System.err.println("Failed to create S3 bucket: " + e.awsErrorDetails().errorMessage());
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Invalid userId format. Bucket creation skipped.");
        }
    }


}
