package com.example.leisuretimemanagement.controller;

import com.example.leisuretimemanagement.logic.TaskService;
import com.example.leisuretimemanagement.model.Task;
import com.example.leisuretimemanagement.model.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.concurrent.CompletableFuture;


@RestController
@RequestMapping("/tasks")
public class TaskController {
    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);
    private final ApplicationEventPublisher eventPublisher;
    private final TaskRepository taskRepository;
    private final TaskService service;

    TaskController(ApplicationEventPublisher eventPublisher, TaskRepository taskRepository, TaskService service){
        this.eventPublisher = eventPublisher;
        this.taskRepository = taskRepository;
        this.service = service;
    }

    @GetMapping(params={"!sort", "!page", "!size"})
    CompletableFuture<ResponseEntity<List<Task>>> readAllTasks(){
        logger.warn("Exposing all the task!");
        return service.findAllAsync()
                .thenApply(ResponseEntity::ok);
    }

    @GetMapping
    ResponseEntity<List<Task>> readAllTasks(Pageable page){
        logger.warn("Custom pageable");
        return ResponseEntity.ok(taskRepository.findAll(page).getContent());
    }

    @GetMapping("/{id}")
    ResponseEntity<Task> readOneTask(@PathVariable int id){
        return taskRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/search/done")
    ResponseEntity<List<Task>> readDoneTasks(@RequestParam(defaultValue = "true") boolean state){
        return ResponseEntity.ok(taskRepository.findByDone(state));
    }

    @PostMapping
    ResponseEntity<Task> createTask(@RequestBody @Valid Task toCreate){
        Task result = taskRepository.save(toCreate);

        return ResponseEntity.created(URI.create("/"+result.getId())).body(result);
    }

    @Transactional
    @PutMapping("/{id}")
    public ResponseEntity<?> updateTask(@PathVariable int id, @RequestBody @Valid Task toUpdate){
        logger.info("PUT method work");
        if(!taskRepository.existsById(id)){
            return ResponseEntity.notFound().build();
        }else {
            taskRepository
                    .findById(id)
                    .ifPresent(task -> task.updateFrom(toUpdate));
            return ResponseEntity.noContent().build();
        }
    }
    /**
     * Transactional have to be over this method because we need reassurance that all
     * operations should be done
     */
    @Transactional
    @PatchMapping("/{id}")
    public ResponseEntity<?> toggleTask(@PathVariable int id){
        logger.info("PATCH method work and get id: "+id);
        if(!taskRepository.existsById(id)){
            return ResponseEntity.notFound().build();
        }else {
            taskRepository.findById(id)
                    .map(Task::toggle)
                    .ifPresent(eventPublisher::publishEvent);
            return ResponseEntity.noContent().build();
        }
    }
}
