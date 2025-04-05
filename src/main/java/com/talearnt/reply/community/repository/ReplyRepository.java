package com.talearnt.reply.community.repository;

import com.talearnt.reply.community.entity.CommunityReply;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplyRepository extends JpaRepository<CommunityReply, Long> {
}
