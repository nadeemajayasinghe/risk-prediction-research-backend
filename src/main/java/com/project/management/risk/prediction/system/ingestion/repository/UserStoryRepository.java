package com.project.management.risk.prediction.system.ingestion.repository;

import com.project.management.risk.prediction.system.ingestion.entity.UserStory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserStoryRepository extends JpaRepository<UserStory, UUID> {
    List<UserStory> findBySprintId(UUID sprintId);
}
