package com.project.management.risk.prediction.system.ingestion.repository;

import com.project.management.risk.prediction.system.ingestion.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface TaskRepository extends JpaRepository<Task, UUID> {
    List<Task> findByStoryId(UUID storyId);
    List<Task> findByStoryIdIn(List<UUID> storyIds);
}
