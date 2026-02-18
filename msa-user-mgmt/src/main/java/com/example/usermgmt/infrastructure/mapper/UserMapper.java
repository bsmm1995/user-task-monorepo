package com.example.usermgmt.infrastructure.mapper;

import com.example.usermgmt.domain.model.User;
import com.example.usermgmt.infrastructure.adapter.in.rest.dto.PostUserRequest;
import com.example.usermgmt.infrastructure.adapter.in.rest.dto.PutUserRequest;
import com.example.usermgmt.infrastructure.adapter.in.rest.dto.UserResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.factory.Mappers;

@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserResponse toDto(User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toDomain(PostUserRequest dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    User toDomain(PutUserRequest dto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateUserFromDto(PutUserRequest dto, @MappingTarget User user);
}

