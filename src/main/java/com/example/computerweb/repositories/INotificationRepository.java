package com.example.computerweb.repositories;

import com.example.computerweb.models.entity.NotificationEntity;
import com.example.computerweb.models.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


public interface INotificationRepository extends JpaRepository<NotificationEntity , Long> {

    List<NotificationEntity> findAllByUser(UserEntity user);

    NotificationEntity findNotificationEntityById ( Long notificationId);
}
