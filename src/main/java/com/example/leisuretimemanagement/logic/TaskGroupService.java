package com.example.leisuretimemanagement.logic;

import com.example.leisuretimemanagement.model.*;
import com.example.leisuretimemanagement.model.projection.GroupReadModel;
import com.example.leisuretimemanagement.model.projection.GroupTaskReadModel;
import com.example.leisuretimemanagement.model.projection.GroupWriteModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

//@Service
public class TaskGroupService {
    public static final int SIZE_PAGE = 3;
    private final TaskGroupRepository repository;
    private final TaskRepository taskRepository;

    public TaskGroupService(TaskGroupRepository repository,TaskRepository taskRepository) {
        this.repository = repository;
        this.taskRepository = taskRepository;
    }

    public GroupReadModel createGroup(GroupWriteModel source){
        return createGroup(source,null);
    }

    public GroupReadModel createGroup(GroupWriteModel source, Project project) {
        TaskGroup result = repository.save(source.toGroup(project));
        return new GroupReadModel(result);
    }

    public List<GroupReadModel> readAll(){
        return repository.findAll().stream()
                .map(GroupReadModel::new)
                .collect(Collectors.toList());
    }

    public Page<TaskGroup> readPage(int currentPage){
        Pageable page = PageRequest.of(currentPage - 1, SIZE_PAGE);
        return repository.findAllByDoneFalse(page);
    }

    public List<GroupReadModel> mapPageTaskGroupIntoDTO(Page<TaskGroup> taskGroups){
        return taskGroups.stream()
                .map(GroupReadModel::new)
                .peek(group -> group.setTasks(group.getTasks().stream()
                        .sorted(Comparator.comparing(GroupTaskReadModel::getDescription))
                        .collect(Collectors.toList())))
                .collect(Collectors.toList());
    }

    @Transactional
    public void toggleGroup(int groupId){
        if(taskRepository.existsByDoneIsFalseAndGroup_Id(groupId)){
            throw new IllegalStateException("Group has undone tasks. Done all the tasks first");
        }
        TaskGroup result = repository.findById(groupId)
                .orElseThrow(() -> new IllegalArgumentException("TaskGroup with given id not found"));
        result.setDone(!result.isDone());
        repository.save(result);
    }
}
