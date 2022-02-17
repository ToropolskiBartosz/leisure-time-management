package com.example.leisuretimemanagement.logic;

import com.example.leisuretimemanagement.TaskConfigurationProperties;
import com.example.leisuretimemanagement.model.*;
import com.example.leisuretimemanagement.model.projection.GroupReadModel;
import com.example.leisuretimemanagement.model.projection.GroupTaskWriteModel;
import com.example.leisuretimemanagement.model.projection.GroupWriteModel;
import com.example.leisuretimemanagement.model.projection.ProjectWriteModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

//@Service
public class ProjectService {
    public static final int SIZE_PAGE = 3;
    private final ProjectRepository repository;
    private final TaskGroupRepository taskGroupRepository;
    private final TaskConfigurationProperties config;
    private final TaskGroupService service;

    public ProjectService(ProjectRepository repository, TaskGroupRepository taskGroupRepository, TaskConfigurationProperties config, TaskGroupService service) {
        this.repository = repository;
        this.taskGroupRepository = taskGroupRepository;
        this.config = config;
        this.service = service;
    }

    public List<Project> readAll(){
        return repository.findAll();
    }

    private Pageable getPageable(int currentPage) {
        return PageRequest.of(currentPage - 1, SIZE_PAGE);
    }

    public Page<Project> readPage(int currentPage){
        return repository.findAll(getPageable(currentPage));
    }

    public Page<Project> readAllPrepared(int currentPage ,String descriptionProject){
        System.out.println(descriptionProject);
        if(descriptionProject != null) {
            return !descriptionProject.isBlank() & currentPage>0 ?
                    repository.findAll(descriptionProject, getPageable(currentPage)) : readPage(currentPage);
        }
        return readPage(currentPage);
    }

    public void deleteProject(int id){
        if(repository.findById(id).isPresent()){
            repository.deleteById(id);
        }
    }

    public Project save(ProjectWriteModel toSave){
        return repository.save(toSave.toProject());
    }

    public ProjectWriteModel getById(int id){
        return repository.findById(id)
                .map(p -> {
                    var project = new ProjectWriteModel();
                    project.setDescription(p.getDescription());
                    project.setSteps(new ArrayList<>(p.getSteps()));
                    return project;
                })
                .orElseThrow(() -> new IllegalArgumentException("Project with given id not found"));
    }

    public void updateProject(int id, ProjectWriteModel current) {
        var result = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Project with given id not found"));
        result.setDescription(current.getDescription());
        current.getSteps().forEach(step -> step.setProject(result));
        current.getSteps().forEach(s->System.out.println(s.getId()));
        result.setSteps(new HashSet<>(current.getSteps()));
        repository.save(result);
    }

    public GroupReadModel createGroup(LocalDateTime deadline, int projectId){
        if(!config.getTemplate().isAllowMultipleTasks()
                && taskGroupRepository.existsByDoneIsFalseAndProject_Id(projectId)){
            throw new IllegalStateException("Only one undone group from project is allowed");
        }
        return repository.findById(projectId)
                .map(project -> {
                    var targetGroup = new GroupWriteModel();
                    targetGroup.setDescription(project.getDescription());
                    targetGroup.setTasks(
                            project.getSteps().stream()
                                    .map(step -> {
                                        var task = new GroupTaskWriteModel();
                                        task.setDescription(step.getDescription());
                                        task.setDeadline(deadline.plusDays(step.getDaysToDeadline()));
                                        return task;
                                    }
                                    ).collect(Collectors.toList())
                    );
                    return service.createGroup(targetGroup, project);
                }).orElseThrow(() -> new IllegalArgumentException("Project with given id not found"));
    }
}
