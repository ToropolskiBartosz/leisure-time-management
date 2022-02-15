package com.example.leisuretimemanagement.adapter;

import com.example.leisuretimemanagement.model.Project;
import com.example.leisuretimemanagement.model.ProjectRepository;
import com.example.leisuretimemanagement.model.TaskGroup;
import com.example.leisuretimemanagement.model.TaskGroupRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface SqlProjectRepository extends ProjectRepository,JpaRepository<Project,Integer> {

    @Override
    @Query("select distinct p from Project p join fetch p.steps")
    List<Project> findAll();

    @Override
    @Query("select distinct p from Project p join fetch p.steps s where p.description like %?1%"
            +" or s.description like %?1%")
    List<Project> findAll(String description);

    @Override
    @Query(nativeQuery = true,
            value ="SELECT * FROM PROJECTS p JOIN PROJECT_STEPS s " +
                    " ON p.ID = s.PROJECT_ID "
                    + "WHERE p.DESCRIPTION LIKE %:description%")
    Page<Project> findAll(@Param("description") String description, Pageable page);
}
