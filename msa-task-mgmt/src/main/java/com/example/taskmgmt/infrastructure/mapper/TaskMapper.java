package com.example.taskmgmt.infrastructure.mapper;

import com.example.taskmgmt.domain.model.Task;
import com.example.taskmgmt.infrastructure.adapter.in.rest.dto.PostTaskRequest;
import com.example.taskmgmt.infrastructure.adapter.in.rest.dto.PutTaskRequest;
import com.example.taskmgmt.infrastructure.adapter.in.rest.dto.TaskResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TaskMapper {
    TaskMapper INSTANCE = Mappers.getMapper(TaskMapper.class);

    TaskResponse toDto(Task task);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Task toDomain(PostTaskRequest dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Task toDomain(PutTaskRequest dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateTaskFromDto(PutTaskRequest dto, @MappingTarget Task task);
}

