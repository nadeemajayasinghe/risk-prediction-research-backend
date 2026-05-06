package com.project.management.risk.prediction.system.ingestion.repository;

import com.project.management.risk.prediction.system.ingestion.entity.Comment;
import com.project.management.risk.prediction.system.ingestion.entity.CommentParentType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {
    List<Comment> findByParentTypeAndParentIdOrderByCreatedAtDesc(CommentParentType parentType, UUID parentId);
    List<Comment> findByParentTypeAndParentIdIn(CommentParentType parentType, List<UUID> parentIds);
}
