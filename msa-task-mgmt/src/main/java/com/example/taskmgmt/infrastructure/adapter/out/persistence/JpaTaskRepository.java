package com.example.taskmgmt.infrastructure.adapter.out.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaTaskRepository extends JpaRepository<TaskEntity, Long> {
    Page<TaskEntity> findByTitleContainingIgnoreCase(@Param("title") String title, Pageable pageable);

    Page<TaskEntity> findByUserId(@Param("userId") Long userId, Pageable pageable);
}


