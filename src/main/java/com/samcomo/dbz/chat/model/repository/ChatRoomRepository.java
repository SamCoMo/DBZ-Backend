package com.samcomo.dbz.chat.model.repository;

import com.samcomo.dbz.chat.model.entity.ChatRoom;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

public interface ChatRoomRepository extends MongoRepository<ChatRoom, String> {

  @Query(value = "{ 'memberEmailList': ?0 }", sort = "{ 'lastChatMessageAt': -1 }")
  List<ChatRoom> findByMemberEmailSortedByLastChatMessageAtDesc(String memberEmail);
}
