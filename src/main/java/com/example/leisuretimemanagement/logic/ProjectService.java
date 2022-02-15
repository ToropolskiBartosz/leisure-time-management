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

import java.time.LocalDateTime;
import java.util.List;
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

    private Pageable getPageable(int currentPage) {
        return PageRequest.of(currentPage - 1, SIZE_PAGE);
    }

    public List<Project> readAll(){
        return repository.findAll();
    }

    public Page<Project> readAllPrepared(int currentPage ,String descriptionProject){
        System.out.println(descriptionProject);
        if(descriptionProject != null) {
            return !descriptionProject.isBlank() & currentPage>0 ?
                    repository.findAll(descriptionProject, getPageable(currentPage)) : readPage(currentPage);
        }
        return readPage(currentPage);
    }

    public Page<Project> readPage(int currentPage){
        return repository.findAll(getPageable(currentPage));
    }

    public Project save(ProjectWriteModel toSave){
        return repository.save(toSave.toProject());
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
