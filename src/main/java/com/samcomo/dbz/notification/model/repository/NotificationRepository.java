package com.samcomo.dbz.notification.model.repository;

import com.samcomo.dbz.notification.model.entity.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NotificationRepository extends MongoRepository<Notification, String> {

}
