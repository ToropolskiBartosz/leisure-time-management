package com.example.leisuretimemanagement.logic;

import com.example.leisuretimemanagement.TaskConfigurationProperties;
import com.example.leisuretimemanagement.model.ProjectRepository;
import com.example.leisuretimemanagement.model.TaskGroupRepository;
import com.example.leisuretimemanagement.model.TaskRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LogicConfiguration {
    @Bean
    ProjectService projectService(
            final ProjectRepository repository,
            final TaskGroupRepository taskGroupRepository,
            final TaskConfigurationProperties config,
            final TaskGroupService service
    ) {return new ProjectService(repository, taskGroupRepository, config, service);}

    @Bean
    TaskGroupService taskGroupService(
            final TaskGroupRepository repository,
            final TaskRepository taskRepository
    ){ return new TaskGroupService(repository,taskRepository);}

    @Bean
    TaskService taskService(final TaskRepository repository){
        return new TaskService(repository);
    }

}
