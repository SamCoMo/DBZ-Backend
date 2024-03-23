package com.samcomo.dbz.notification.model.repository;

import com.samcomo.dbz.notification.model.entity.Notification;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NotifiRepository extends MongoRepository<Notification, String> {

  List<Notification> findAllByMemberId(String memberId);

}
