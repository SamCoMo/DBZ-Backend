package com.samcomo.dbz.chat.model.repository;

import com.samcomo.dbz.chat.model.entity.ChatRoom;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {
  List<ChatRoom> findBySenderIdOrRecipientId(String userId, String userId1);
}
