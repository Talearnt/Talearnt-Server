package com.talearnt.comment.community.repository;

import com.talearnt.comment.community.entity.CommunityComment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<CommunityComment, Long> {
}
