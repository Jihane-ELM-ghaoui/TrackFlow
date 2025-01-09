package com.example.devsecops;

import com.example.devsecops.kafkaConfig.KafkaProducer;
import com.example.devsecops.model.User;
import com.example.devsecops.repository.UserRepo;
import com.example.devsecops.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import software.amazon.awssdk.services.s3.S3Client;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;

class UserServiceImplTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private KafkaProducer kafkaProducer;

    @Mock
    private S3Client s3Client;

    @Mock
    private JwtAuthenticationToken jwtAuthenticationToken;

    @Mock
    private Jwt jwt;

    private UserServiceImpl userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userService = new UserServiceImpl(s3Client);
        userService.userRepo = userRepo;
        userService.kafkaProducer = kafkaProducer;
    }

//    @Test
//    void saveUser_shouldSaveUserAndTriggerDependencies() {
//        String userId = "auth0|670a49f45fb7f3ba271f916a";
//        String email = "admin@gmail.com";
//        String fullName = "admin ";
//        boolean emailVerified = false;
//
//        when(jwtAuthenticationToken.getName()).thenReturn(userId);
//        when(jwtAuthenticationToken.getToken()).thenReturn(jwt);
//        when(jwt.getClaim("email")).thenReturn(email);
//        when(jwt.getClaim("email_verified")).thenReturn(emailVerified);
//
//        Map<String, Object> userMetadata = Map.of(
//                "Date_Of_Birthday", "2024-10-08",
//                "Full_Name", fullName,
//                "Organisation", "GS",
//                "Phone_Number", "+212612345678",
//                "job", "designer"
//        );
//
//        Map<String, Object> claims = Map.of("https://demo.app.com/user_metadata", userMetadata);
//        when(jwt.getClaims()).thenReturn(claims);
//
//        System.out.println("Mocked claims: " + claims);
//
//        SecurityContextHolder.getContext().setAuthentication(jwtAuthenticationToken);
//
//        when(userRepo.existsByEmail(email)).thenReturn(false);
//        when(userRepo.existsByUserId(userId)).thenReturn(false);
//
//        userService.saveUser();
//
//        verify(userRepo).save(any(User.class));
//        verify(kafkaProducer).sendBucketCreateMessage(userId);
//        verify(kafkaProducer).sendEmailVerificationMessage(userId);
//    }



//    @Test
    void saveUser_shouldNotSaveUserIfEmailExists() {
        // Prepare JWT token mock
        String userId = "auth0|670a49f45fb7f3ba271f916a";
        String email = "admin@gmail.com";

        when(jwtAuthenticationToken.getName()).thenReturn(userId);
        when(jwtAuthenticationToken.getToken()).thenReturn(jwt);
        when(jwt.getClaim("email")).thenReturn(email);

        when(userRepo.existsByEmail(email)).thenReturn(true);


        Map<String, Object> userMetadata = Map.of(
                "Date_Of_Birthday", "2024-10-08",
                "Full_Name", "admin",
                "Organisation", "GS",
                "Phone_Number", "+212612345678",
                "job", "designer"
        );

        Map<String, Object> claims = Map.of("https://demo.app.com/user_metadata", userMetadata);
        when(jwt.getClaims()).thenReturn(claims);

        SecurityContextHolder.getContext().setAuthentication(jwtAuthenticationToken);

        userService.saveUser();

        verify(userRepo, never()).save(any(User.class)); // User should not be saved
        verify(kafkaProducer, never()).sendBucketCreateMessage(any()); // S3 and Kafka should not be called
    }
}
