package com.example.usermgmt.infrastructure.mapper;

import com.example.usermgmt.infrastructure.adapter.in.rest.dto.Pagination;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import org.springframework.data.domain.Page;

/**
 * Mapper para convertir información de paginación desde Page<T> a DTO Pagination.
 */
@Mapper
public interface PaginationMapper {
    PaginationMapper INSTANCE = Mappers.getMapper(PaginationMapper.class);

    /**
     * Mapea un Page a un DTO Pagination.
     *
     * @param page la página del repositorio
     * @return dto de paginación
     */
    default Pagination toPaginationDto(Page<?> page) {
        var pagination = new Pagination();
        pagination.setPage(page.getNumber());
        pagination.setSize(page.getSize());
        pagination.setTotalElements(page.getTotalElements());
        pagination.setTotalPages(page.getTotalPages());
        return pagination;
    }
}

