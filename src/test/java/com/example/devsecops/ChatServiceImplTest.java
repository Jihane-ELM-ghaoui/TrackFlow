package com.example.devsecops;

import com.example.devsecops.kafkaConfig.KafkaProducer;
import com.example.devsecops.model.ChatMessage;
import com.example.devsecops.repo.ChatMessageRepository;
import com.example.devsecops.service.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.LocalDateTime;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ChatServiceImplTest {

    @Mock
    private ChatMessageRepository messageRepository;

    @Mock
    private KafkaProducer kafkaProducer;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private JwtAuthenticationToken jwtAuthenticationToken;

    @Mock
    private Jwt jwt;

    @InjectMocks
    private MessageService messageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(jwtAuthenticationToken);
    }

    @Test
    void testSaveMessage() {
        ChatMessage message = new ChatMessage();
        message.setSendername("sender");
        message.setReceiver("receiver");

        messageService.saveMessage(message);

        assertNotNull(message.getTimestamp());
        verify(messageRepository).save(message);
        verify(kafkaProducer).sendChatMessage("sender", "receiver");
    }

    @Test
    void testGetUsersByConversation() {
        String userId = "auth0|670a49f45fb7f3ba271f916a";
        String email = "admin@gmail.com";

        when(jwtAuthenticationToken.getName()).thenReturn(userId);
        when(jwtAuthenticationToken.getToken()).thenReturn(jwt);
        when(jwt.getClaim("email")).thenReturn(email);

        List<String> senders = List.of("receiver1", "receiver2");
        List<String> receivers = List.of("sender1", "sender2");
        List<String> sendersId = List.of("id1", "id2");
        List<String> receiversId = List.of("id3", "id4");

        when(messageRepository.findDistinctSendersByReceiver(userId)).thenReturn(senders);
        when(messageRepository.findDistinctReceiversBySender(userId)).thenReturn(receivers);
        when(messageRepository.findDistinctSendersidByReceiverid(userId)).thenReturn(sendersId);
        when(messageRepository.findDistinctReceiversidBySenderid(userId)).thenReturn(receiversId);

        when(messageRepository.findLastMessageBetweenUsers(eq(userId), anyString())).thenReturn("Last Message");
        when(messageRepository.findLastMessageTimeBetweenUsers(eq(userId), anyString())).thenReturn(LocalDateTime.now());

        Map<String, Object> userMetadata = Map.of(
                "Date_Of_Birthday", "2024-10-08",
                "Full_Name", "admin",
                "Organisation", "GS",
                "Phone_Number", "+212612345678",
                "job", "designer"
        );

        Map<String, Object> claims = Map.of("https://demo.app.com/user_metadata", userMetadata);
        when(jwt.getClaims()).thenReturn(claims);

        List<Map<String, String>> result = messageService.getUsersByConversation();

        assertNotNull(result);
        assertEquals(4, result.size());
    }
}
