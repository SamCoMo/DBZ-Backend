package com.samcomo.dbz.chat.model.repository;

import com.samcomo.dbz.chat.model.entity.ChatMessage;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

  List<ChatMessage> findByChatRoomId(String chatRoomId);

  Slice<ChatMessage> findByChatRoomId(String chatRoomId, Pageable pageable);

  Optional<ChatMessage> findFirstByChatRoomIdOrderByCreatedAtDesc(String chatRoomId);
}
