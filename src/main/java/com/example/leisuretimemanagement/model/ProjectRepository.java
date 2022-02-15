package com.example.leisuretimemanagement.model;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ProjectRepository {
    List<Project> findAll();

    Optional<Project> findById(Integer id);

    Project save(Project entity);

    Page<Project> findAll(String description, Pageable page);

    Page<Project> findAll(Pageable page);

    List<Project> findAll(String description);
}
