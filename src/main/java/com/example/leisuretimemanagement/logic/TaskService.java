package com.example.leisuretimemanagement.logic;

import com.example.leisuretimemanagement.model.Task;
import com.example.leisuretimemanagement.model.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.CompletableFuture;

//@Service
public class TaskService {
    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);
    private final TaskRepository repository;
    private final ApplicationEventPublisher eventPublisher;


    public TaskService(TaskRepository repository, ApplicationEventPublisher eventPublisher) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
    }

    @Async
    public CompletableFuture<List<Task>> findAllAsync(){
        logger.info("Supply async");
        return CompletableFuture.supplyAsync(repository::findAll);
    }

    @Transactional
    public void toggleTask(int taskId){
        repository.findById(taskId)
                .map(Task::toggle)
                .ifPresent(eventPublisher::publishEvent);
    }
}
