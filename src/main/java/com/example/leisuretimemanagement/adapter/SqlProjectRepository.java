package com.example.leisuretimemanagement.adapter;

import com.example.leisuretimemanagement.model.Project;
import com.example.leisuretimemanagement.model.ProjectRepository;
import com.example.leisuretimemanagement.model.TaskGroup;
import com.example.leisuretimemanagement.model.TaskGroupRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
interface SqlProjectRepository extends ProjectRepository,JpaRepository<Project,Integer> {

    @Override
    @Query("select distinct p from Project p join fetch p.steps")
    List<Project> findAll();

}
