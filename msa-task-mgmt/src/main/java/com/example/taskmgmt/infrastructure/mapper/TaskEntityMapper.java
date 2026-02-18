package com.example.taskmgmt.infrastructure.mapper;

import com.example.taskmgmt.domain.model.Task;
import com.example.taskmgmt.infrastructure.adapter.out.persistence.TaskEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper
public interface TaskEntityMapper {
    TaskEntityMapper INSTANCE = Mappers.getMapper(TaskEntityMapper.class);

    @Mapping(target = "createdAt", expression = "java(toOffsetDateTime(entity.getCreatedAt()))")
    @Mapping(target = "updatedAt", expression = "java(toOffsetDateTime(entity.getUpdatedAt()))")
    Task toDomain(TaskEntity entity);

    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    TaskEntity toEntity(Task task);

    default OffsetDateTime toOffsetDateTime(LocalDateTime localDateTime) {
        return localDateTime != null ? localDateTime.atOffset(ZoneOffset.UTC) : null;
    }
}

