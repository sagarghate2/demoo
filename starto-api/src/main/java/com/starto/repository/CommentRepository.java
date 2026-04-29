package com.starto.repository;

import com.starto.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CommentRepository extends JpaRepository<Comment, UUID> {

    // get only top level comments (not replies)
    List<Comment> findBySignalIdAndParentIdIsNullOrderByCreatedAtDesc(UUID signalId);

    List<Comment> findBySpaceIdAndParentIdIsNullOrderByCreatedAtDesc(UUID spaceId);

    // get replies for a comment
    List<Comment> findByParentIdOrderByCreatedAtAsc(UUID parentId);

    int countBySignalId(UUID signalId);
    int countBySpaceId(UUID spaceId);

    @org.springframework.transaction.annotation.Transactional
    @org.springframework.data.jpa.repository.Modifying
    void deleteBySignalId(UUID signalId);

    @org.springframework.transaction.annotation.Transactional
    @org.springframework.data.jpa.repository.Modifying
    void deleteBySpaceId(UUID spaceId);
}