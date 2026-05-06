package com.project.management.risk.prediction.system.sprint.service;

import com.project.management.risk.prediction.system.common.exception.ConflictException;
import com.project.management.risk.prediction.system.common.exception.ResourceNotFoundException;
import com.project.management.risk.prediction.system.sprint.dto.SprintCreateRequest;
import com.project.management.risk.prediction.system.sprint.dto.SprintResponse;
import com.project.management.risk.prediction.system.sprint.dto.SprintStatusUpdateRequest;
import com.project.management.risk.prediction.system.sprint.dto.SprintUpdateRequest;
import com.project.management.risk.prediction.system.sprint.entity.Sprint;
import com.project.management.risk.prediction.system.sprint.entity.SprintStatus;
import com.project.management.risk.prediction.system.sprint.mapper.SprintMapper;
import com.project.management.risk.prediction.system.sprint.repository.SprintRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class SprintService {

    private final SprintRepository sprintRepository;
    private final SprintMapper sprintMapper;

    public SprintResponse create(SprintCreateRequest req) {
        if (req.endDate().isBefore(req.startDate())) {
            throw new ConflictException("endDate must be on or after startDate");
        }
        Sprint sprint = sprintMapper.toEntity(req);
        sprint.setStatus(SprintStatus.PLANNED);
        return sprintMapper.toResponse(sprintRepository.save(sprint));
    }

    @Transactional(readOnly = true)
    public SprintResponse getById(UUID id) {
        return sprintMapper.toResponse(loadOrThrow(id));
    }

    @Transactional(readOnly = true)
    public Sprint loadOrThrow(UUID id) {
        return sprintRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sprint", id));
    }

    @Transactional(readOnly = true)
    public Page<SprintResponse> list(String projectKey, SprintStatus status, Pageable pageable) {
        Page<Sprint> page;
        if (projectKey != null && status != null) {
            page = sprintRepository.findByProjectKeyAndStatus(projectKey, status, pageable);
        } else if (projectKey != null) {
            page = sprintRepository.findByProjectKey(projectKey, pageable);
        } else if (status != null) {
            page = sprintRepository.findByStatus(status, pageable);
        } else {
            page = sprintRepository.findAll(pageable);
        }
        return page.map(sprintMapper::toResponse);
    }

    public SprintResponse update(UUID id, SprintUpdateRequest req) {
        Sprint sprint = loadOrThrow(id);
        if (req.name() != null) sprint.setName(req.name());
        if (req.goal() != null) sprint.setGoal(req.goal());
        if (req.startDate() != null) sprint.setStartDate(req.startDate());
        if (req.endDate() != null) sprint.setEndDate(req.endDate());
        if (sprint.getEndDate().isBefore(sprint.getStartDate())) {
            throw new ConflictException("endDate must be on or after startDate");
        }
        return sprintMapper.toResponse(sprint);
    }

    public SprintResponse updateStatus(UUID id, SprintStatusUpdateRequest req) {
        Sprint sprint = loadOrThrow(id);
        validateTransition(sprint.getStatus(), req.status());
        sprint.setStatus(req.status());
        return sprintMapper.toResponse(sprint);
    }

    private void validateTransition(SprintStatus from, SprintStatus to) {
        if (from == to) return;
        boolean ok = switch (from) {
            case PLANNED -> to == SprintStatus.ACTIVE || to == SprintStatus.CANCELLED;
            case ACTIVE -> to == SprintStatus.COMPLETED || to == SprintStatus.CANCELLED;
            case COMPLETED, CANCELLED -> false;
        };
        if (!ok) {
            throw new ConflictException("Invalid sprint status transition: " + from + " -> " + to);
        }
    }
}
