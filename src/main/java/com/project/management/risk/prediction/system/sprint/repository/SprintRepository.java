package com.project.management.risk.prediction.system.sprint.repository;

import com.project.management.risk.prediction.system.sprint.entity.Sprint;
import com.project.management.risk.prediction.system.sprint.entity.SprintStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface SprintRepository extends JpaRepository<Sprint, UUID> {
    Page<Sprint> findByProjectKey(String projectKey, Pageable pageable);
    Page<Sprint> findByProjectKeyAndStatus(String projectKey, SprintStatus status, Pageable pageable);
    Page<Sprint> findByStatus(SprintStatus status, Pageable pageable);
}
