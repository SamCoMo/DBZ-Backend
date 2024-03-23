package com.samcomo.dbz.notification.model.repository;

import com.samcomo.dbz.notification.model.entity.Notifi;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface NotifiRepository extends MongoRepository<Notifi, String> {

  List<Notifi> findAllByMemberId(Long memberId);

}
