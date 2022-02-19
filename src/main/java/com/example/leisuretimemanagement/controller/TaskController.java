package com.example.leisuretimemanagement.controller;

import com.example.leisuretimemanagement.logic.TaskService;
import com.example.leisuretimemanagement.model.Project;
import com.example.leisuretimemanagement.model.Task;
import com.example.leisuretimemanagement.model.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;


@Controller
@RequestMapping("/tasks")
public class TaskController {
    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);
    private final TaskRepository taskRepository;
    private final TaskService service;

    TaskController(TaskRepository taskRepository, TaskService service){
        this.taskRepository = taskRepository;
        this.service = service;
    }

    @GetMapping
    String readAllTask(Model model){
        model.addAttribute("fromGroupId","");
        return "tasks";
    }

    @GetMapping("/{id}")
    String readAllTask(@PathVariable int id,
                       Model model){
        model.addAttribute("tasks", taskRepository.findAllByGroup_Id(id));
        model.addAttribute("fromGroupId",id);
        return "tasks";
    }

    @GetMapping("/toggle/{id}")
    public String toggleTask(@PathVariable int id,
            @Param("fromGroupId") String fromGroupId,
                      Model model){
        logger.info("toggleTask with id: "+id);
        if(!taskRepository.existsById(id)){
            logger.info("toggleTask with id: "+id);
        }else {
            service.toggleTask(id);
            if(!fromGroupId.isBlank()){
                int groupId = Integer.parseInt(fromGroupId);
                model.addAttribute("fromGroupId",groupId);
                model.addAttribute("tasks",taskRepository.findAllByGroup_Id(groupId));
            }else{
                model.addAttribute("fromGroupId","");
                model.addAttribute("tasks",getTasks());
            }
        }
        return "tasks";
    }

    @ModelAttribute("tasks")
    List<Task> getTasks(){
        return taskRepository.findAll();
    }

//
//    @GetMapping(params={"!sort", "!page", "!size"})
//    CompletableFuture<ResponseEntity<List<Task>>> readAllTasks(){
//        logger.warn("Exposing all the task!");
//        return service.findAllAsync()
//                .thenApply(ResponseEntity::ok);
//    }
//
//    @GetMapping
//    ResponseEntity<List<Task>> readAllTasks(Pageable page){
//        logger.warn("Custom pageable");
//        return ResponseEntity.ok(taskRepository.findAll(page).getContent());
//    }
//
//    @GetMapping("/{id}")
//    ResponseEntity<Task> readOneTask(@PathVariable int id){
//        return taskRepository.findById(id)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }
//
//    @GetMapping("/search/done")
//    ResponseEntity<List<Task>> readDoneTasks(@RequestParam(defaultValue = "true") boolean state){
//        return ResponseEntity.ok(taskRepository.findByDone(state));
//    }
//
//    @PostMapping
//    ResponseEntity<Task> createTask(@RequestBody @Valid Task toCreate){
//        Task result = taskRepository.save(toCreate);
//
//        return ResponseEntity.created(URI.create("/"+result.getId())).body(result);
//    }
//
//    @Transactional
//    @PutMapping("/{id}")
//    public ResponseEntity<?> updateTask(@PathVariable int id, @RequestBody @Valid Task toUpdate){
//        logger.info("PUT method work");
//        if(!taskRepository.existsById(id)){
//            return ResponseEntity.notFound().build();
//        }else {
//            taskRepository
//                    .findById(id)
//                    .ifPresent(task -> task.updateFrom(toUpdate));
//            return ResponseEntity.noContent().build();
//        }
//    }
//    /**
//     * Transactional have to be over this method because we need reassurance that all
//     * operations should be done
//     */
//    @Transactional
//    @PatchMapping("/{id}")
//    public ResponseEntity<?> toggleTask(@PathVariable int id){
//        logger.info("PATCH method work and get id: "+id);
//        if(!taskRepository.existsById(id)){
//            return ResponseEntity.notFound().build();
//        }else {
//            taskRepository.findById(id)
//                    .map(Task::toggle)
//                    .ifPresent(eventPublisher::publishEvent);
//            return ResponseEntity.noContent().build();
//        }
//    }
}
