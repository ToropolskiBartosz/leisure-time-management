package com.example.leisuretimemanagement.controller;

import com.example.leisuretimemanagement.logic.ProjectService;
import com.example.leisuretimemanagement.model.Project;
import com.example.leisuretimemanagement.model.ProjectStep;
import com.example.leisuretimemanagement.model.projection.ProjectWriteModel;
import io.micrometer.core.annotation.Timed;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Controller
//@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequestMapping("/projects")
public class ProjectController {
    private final ProjectService service;

    public ProjectController(ProjectService service) {
        this.service = service;
    }

    @GetMapping
    String showProject(Model model,
                       @Param("description") String description){
        return showCurrentPageProject(model,1, description);
    }

    @GetMapping("/{page}")
    String showCurrentPageProject(Model model,
                                  @PathVariable("page") int currentPage,
                                  @Param("description") String description){
        Page<Project> pages = service.readAllPrepared(currentPage, description);
        long totalElements = pages.getTotalElements();
        int totalPages = pages.getTotalPages();
        //model.addAttribute("projects", service.readAllByDescription(description));
        model.addAttribute("projects", pages);
        model.addAttribute("description", description);
        model.addAttribute("totalElements", totalElements);
        model.addAttribute("currentPage", currentPage);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("project", new ProjectWriteModel());
        return "projects";
    }


    @PostMapping
    String addProject(
            @ModelAttribute("project") @Valid ProjectWriteModel current,
            BindingResult bindingResult,
            Model model){
        if(bindingResult.hasErrors()) {
            System.out.println("czy to działa");
            return "projects";
        }
        service.save(current);
        model.addAttribute("project", new ProjectWriteModel());
        model.addAttribute("projects",getProjects());
        model.addAttribute("message", "Dodano projekt");
        return "projects";
    }

    @PostMapping(params = "addStep")
    String addProjectStep(@ModelAttribute("project") ProjectWriteModel current){
        current.getSteps().add(new ProjectStep());
        return "projects";
    }

    @Timed(value = "project.create.group", histogram = true, percentiles = {0.5, 0.95, 0.99})
    @PostMapping("/{id}")
    String createGroup(
            @ModelAttribute("project") ProjectWriteModel current,
            Model model,
            @PathVariable int id,
            @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm") LocalDateTime deadline){
        try{
            service.createGroup(deadline,id);
            model.addAttribute("message","Dodano grupę! :D");
        }catch (IllegalStateException | IllegalArgumentException e){
            model.addAttribute("message","Błąd podczas tworzenia grupy! :(");
        }
        return "projects";
    }

    @ModelAttribute("projects")
    List<Project> getProjects(){
        return service.readAll();
    }

}
