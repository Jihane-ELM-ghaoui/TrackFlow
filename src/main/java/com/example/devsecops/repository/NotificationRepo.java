package com.example.devsecops.repository;



import com.example.devsecops.model.Notification;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepo extends JpaRepository<Notification, Integer> {

    List<Notification> findByUserId(String userId);

    Long countByIsReadFalse();

    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true")
    void markAllAsRead();
}
