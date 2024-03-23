package com.samcomo.dbz.notification.model.repository;

import com.samcomo.dbz.notification.model.entity.Noti;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationRepository extends JpaRepository<Noti, Long> {

  List<Noti> findAllByMemberId(Long memberId);

}
