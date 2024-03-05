package com.samcomo.dbz.chat.model.repository;

import com.samcomo.dbz.chat.model.entity.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;
import java.util.Optional;

public interface ChatMessageRepository extends
    MongoRepository<ChatMessage, String> {
  Optional<List<ChatMessage>> findByChatRoomId(String chatRoomId);

  Optional<ChatMessage> findFirstByChatRoomIdOrderByCreatedAtDesc(String chatRoomId);
}
