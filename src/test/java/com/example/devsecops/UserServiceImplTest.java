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

        // Initialize userService with mocked dependencies
        userService = new UserServiceImpl(s3Client);
        userService.userRepo = userRepo;
        userService.kafkaProducer = kafkaProducer;
    }

    @Test
    void saveUser_shouldNotSaveUserIfEmailExists() {
        // Prepare JWT token mock
        String userId = "auth0|670a49f45fb7f3ba271f916a";
        String email = "admin@gmail.com";

        // Mock JwtAuthenticationToken directly
        when(jwtAuthenticationToken.getName()).thenReturn(userId);
        when(jwtAuthenticationToken.getToken()).thenReturn(jwt);
        when(jwt.getClaim("email")).thenReturn(email);

        // Mock repository behavior to simulate user existence check
        when(userRepo.existsByEmail(email)).thenReturn(true);


        // Mock claims for user_metadata
        Map<String, Object> userMetadata = Map.of(
                "Date_Of_Birthday", "2024-10-08",
                "Full_Name", "admin",
                "Organisation", "GS",
                "Phone_Number", "+212612345678",
                "job", "designer"
        );

        // Mock the claims map to include the "https://demo.app.com/user_metadata" key
        Map<String, Object> claims = Map.of("https://demo.app.com/user_metadata", userMetadata);
        when(jwt.getClaims()).thenReturn(claims);

        // Set the mocked authentication in SecurityContextHolder
        SecurityContextHolder.getContext().setAuthentication(jwtAuthenticationToken);

        // Execute the method
        userService.saveUser();

        // Verify that no user is saved when email exists
        verify(userRepo, never()).save(any(User.class)); // User should not be saved
        verify(kafkaProducer, never()).sendBucketCreateMessage(any()); // S3 and Kafka should not be called
    }
}
