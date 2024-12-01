package com.example.devsecops.service;


import com.example.devsecops.kafkaConfig.KafkaProducer;
import com.example.devsecops.model.ChatMessage;
import com.example.devsecops.repo.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class MessageService {

    @Autowired
    private ChatMessageRepository messageRepository;


    @Autowired
    private KafkaProducer kafkaProducer;


    // Method to save a new message
    public void saveMessage(ChatMessage message){

        message.setTimestamp(LocalDateTime.now());
        messageRepository.save(message);

        kafkaProducer.sendChatMessage(message.getSendername(), message.getReceiver());
    }



    // Method to retrieve all messages
    public List<ChatMessage> getPrivateMessages(String receiver) {

        if (receiver.startsWith("auth0") && !receiver.startsWith("auth0|")) {
            receiver = receiver.replace("auth0", "auth0|");
        }


        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String sender = auth.getName();

        List<ChatMessage> sentMessages = messageRepository.findAllBySenderAndReceiverOrderByTimestampAsc(sender, receiver);
        List<ChatMessage> receivedMessages = messageRepository.findAllByReceiverAndSenderOrderByTimestampAsc(sender, receiver);

        List<ChatMessage> allMessages = new ArrayList<>(sentMessages);
        allMessages.addAll(receivedMessages);
        allMessages.sort(Comparator.comparing(ChatMessage::getTimestamp)); // Ensure messages are sorted by timestamp

        return allMessages;
    }






    public List<Map<String, String>> getUsersByConversation() {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String userId = auth.getName();

        // Retrieve senders and receivers related to the user
        List<String> senders = messageRepository.findDistinctSendersByReceiver(userId);
        List<String> receivers = messageRepository.findDistinctReceiversBySender(userId);

        List<String> sendersId = messageRepository.findDistinctSendersidByReceiverid(userId);
        List<String> receiversId = messageRepository.findDistinctReceiversidBySenderid(userId);

        // Combine both usernames and IDs into a list of maps
        List<Map<String, String>> allUsers = new ArrayList<>();

        // Process senders and their IDs
        for (int i = 0; i < senders.size(); i++) {
            Map<String, String> user = new HashMap<>();
            user.put("username", senders.get(i));
            user.put("id", sendersId.get(i));
            allUsers.add(user);
        }

        // Process receivers and their IDs
        for (int i = 0; i < receivers.size(); i++) {
            Map<String, String> user = new HashMap<>();
            user.put("username", receivers.get(i));
            user.put("id", receiversId.get(i));
            allUsers.add(user);
        }

        // Remove duplicates by using a Set
        Set<Map<String, String>> uniqueUsers = new HashSet<>(allUsers);

        System.out.println(allUsers);

        // Convert back to a list
        return new ArrayList<>(uniqueUsers);
    }





}
