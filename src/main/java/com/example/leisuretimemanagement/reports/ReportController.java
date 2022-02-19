package com.example.leisuretimemanagement.reports;

import com.example.leisuretimemanagement.model.Task;
import com.example.leisuretimemanagement.model.TaskRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/reports")
public class ReportController {
    private final TaskRepository taskRepository;
    private final PersistedTaskEventRepository eventRepository;

    public ReportController(TaskRepository taskRepository, PersistedTaskEventRepository eventRepository) {
        this.taskRepository = taskRepository;
        this.eventRepository = eventRepository;
    }

    @GetMapping
    String readReports(Model model){
        List<TaskWithChangesCount> reports = taskRepository.findAll().stream()
                .map(task ->
                        new TaskWithChangesCount(task,
                                eventRepository.countByTaskId(task.getId())
                        )
                ).sorted(Comparator.comparingInt(TaskWithChangesCount::getChangesCount).reversed())
                .collect(Collectors.toList());
        model.addAttribute("reports",reports);
        return "reports";
    }

//    @GetMapping("/count/{id}")
//    ResponseEntity<TaskWithChangesCount> readTaskWithCount(@PathVariable int id){
//        return taskRepository.findById(id)
//                .map(task -> new TaskWithChangesCount(task, eventRepository.countByTaskId(id)))
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }

    private static class TaskWithChangesCount {
        public String description;
        public boolean done;
        public Integer changesCount;

        TaskWithChangesCount(Task task, int changesCount) {
            this.description = task.getDescription();
            this.done = task.isDone();
            this.changesCount = changesCount;
        }

        public Integer getChangesCount() {
            return changesCount;
        }
    }
}
