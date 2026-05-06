package com.project.management.risk.prediction.system.sprint.mapper;

import com.project.management.risk.prediction.system.sprint.dto.SprintCreateRequest;
import com.project.management.risk.prediction.system.sprint.dto.SprintResponse;
import com.project.management.risk.prediction.system.sprint.entity.Sprint;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SprintMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "version", ignore = true)
    Sprint toEntity(SprintCreateRequest request);

    SprintResponse toResponse(Sprint sprint);
}
