package com.example.leisuretimemanagement.controller;

import com.example.leisuretimemanagement.logic.TaskGroupService;
import com.example.leisuretimemanagement.model.ProjectStep;
import com.example.leisuretimemanagement.model.Task;
import com.example.leisuretimemanagement.model.TaskRepository;
import com.example.leisuretimemanagement.model.projection.GroupReadModel;
import com.example.leisuretimemanagement.model.projection.GroupTaskWriteModel;
import com.example.leisuretimemanagement.model.projection.GroupWriteModel;
import com.example.leisuretimemanagement.model.projection.ProjectWriteModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;


@Controller
@IllegalExceptionProcessing
@RequestMapping("/groups")
public class TaskGroupController {
    private static final Logger logger = LoggerFactory.getLogger(TaskGroupController.class);
    private final TaskGroupService service;
    private final TaskRepository taskRepository;

    TaskGroupController(TaskGroupService service, TaskRepository taskRepository){
        this.service = service;
        this.taskRepository = taskRepository;
    }

    @GetMapping(produces = MediaType.TEXT_HTML_VALUE)
    String showGroups(Model model){
        model.addAttribute("group", new GroupWriteModel());
        return "groups";
    }

    @PostMapping(produces = MediaType.TEXT_HTML_VALUE, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    String addGroup(
            @ModelAttribute("group") @Valid GroupWriteModel current,
            BindingResult bindingResult,
            Model model){
        if(bindingResult.hasErrors()) {
            System.out.println("czy to działa");
            return "groups";
        }
        service.createGroup(current);
        model.addAttribute("group", new GroupWriteModel());
        model.addAttribute("groups",getGroups());
        model.addAttribute("message", "Dodano grupę");
        return "groups";
    }

    @PostMapping(params = "addTask",
            produces = MediaType.TEXT_HTML_VALUE,
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    String addGroupTask(@ModelAttribute("group") GroupWriteModel current){
        current.getTasks().add(new GroupTaskWriteModel());
        return "groups";
    }

    @ResponseBody
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<GroupReadModel> createGroup(@RequestBody @Valid GroupWriteModel toCreate){
        GroupReadModel result = service.createGroup(toCreate);
        return ResponseEntity
                .created(URI.create("/" + result.getId()))
                .body(result);
    }

    @ResponseBody
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<GroupReadModel>> readAllGroups(){
        logger.warn("Exposing all the task!");
        return ResponseEntity.ok(service.readAll());
    }
    @ResponseBody
    @GetMapping(value = "/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<Task>> readAllTasksFromGroup(@PathVariable int id){
        return ResponseEntity.ok(taskRepository.findAllByGroup_Id(id));
    }
    @ResponseBody
    @Transactional
    @PatchMapping("/{id}")
    public ResponseEntity<?> toggleGroup(@PathVariable int id){
        logger.warn("PATCH method work and get id: "+id);
        service.toggleGroup(id);
        return ResponseEntity.noContent().build();
    }

    @ModelAttribute("groups")
    List<GroupReadModel> getGroups() {
        return service.readAll();
    }
}
