package com.example.devsecops.repo;

import com.example.devsecops.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {

    List<ChatMessage> findAllBySenderAndReceiverOrderByTimestampAsc(String sender, String receiver);

    List<ChatMessage> findAllByReceiverAndSenderOrderByTimestampAsc(String receiver, String sender);


    @Query("SELECT DISTINCT c.receivername FROM ChatMessage c WHERE c.sender = :sender")
    List<String> findDistinctReceiversBySender(String sender);


    @Query("SELECT DISTINCT c.sendername FROM ChatMessage c WHERE c.receiver = :receiver")
    List<String> findDistinctSendersByReceiver(String receiver);




    @Query("SELECT DISTINCT c.receiver FROM ChatMessage c WHERE c.sender = :sender")
    List<String> findDistinctReceiversidBySenderid(String sender);


    @Query("SELECT DISTINCT c.sender From ChatMessage c WHERE c.receiver = :receiver")
    List<String> findDistinctSendersidByReceiverid(String receiver);




}
