package com.talearnt.chat.repository;

import com.talearnt.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository  extends JpaRepository<ChatRoom, Long> {
}
