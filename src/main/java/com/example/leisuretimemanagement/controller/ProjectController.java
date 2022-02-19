package com.example.leisuretimemanagement.controller;

import com.example.leisuretimemanagement.logic.ProjectService;
import com.example.leisuretimemanagement.model.Project;
import com.example.leisuretimemanagement.model.ProjectStep;
import com.example.leisuretimemanagement.model.projection.ProjectWriteModel;
import io.micrometer.core.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.repository.query.Param;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@IllegalExceptionProcessing
@RequestMapping("/projects")
public class ProjectController {
    private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);

    private final ProjectService service;

    public ProjectController(ProjectService service) {
        this.service = service;
    }

    @GetMapping
    String showProject(Model model){
        return showCurrentPageProject(model,1, "");
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

    @GetMapping("/delete/{id}")
    String deleteProject(Model model,
                         @PathVariable int id){
        logger.info("We get project with id:" + id +" to delete");
        service.deleteProject(id);
        return showProject(model);
    }

    @GetMapping("/edit/{id}")
    String editProject(Model model,
                         @PathVariable int id){
        logger.info("We get project with id:" + id +" to edit");
        ProjectWriteModel source = service.getById(id);
        model.addAttribute("project", source);
        model.addAttribute("id", id);
        return "edit_projects";
    }

    @PostMapping("/edit/{id}")
    String editProjectSave(Model model,
                           @PathVariable int id,
                           @ModelAttribute("project") ProjectWriteModel current){
        service.updateProject(id,current);
        return "edit_projects";
    }

    @PostMapping(path="/edit/{id}",params = "addStep")
    String addProjectStepEdit(Model model,
                              @PathVariable int id,
                              @ModelAttribute("project") ProjectWriteModel current){
        current.getSteps().add(new ProjectStep());
        model.addAttribute("id", id);
        return "edit_projects";
    }

    @GetMapping("/step/delete/{id}/{stepId}")
    String deleteStepFromProject(@PathVariable int id,
                                 @PathVariable int stepId,
                                 Model model){
        service.deleteStepFromProject(id,stepId);
        model.addAttribute("project",service.getById(id));
        model.addAttribute("message","Powiodła się edycja");
        return "edit_projects";
    }


    @PostMapping
    String addProject(
            @ModelAttribute("project") @Valid ProjectWriteModel current,
            BindingResult bindingResult,
            Model model){
        if(bindingResult.hasErrors()) {
            System.out.println("czy to działa");
            return showProject(model);
        }
        service.save(current);
        model.addAttribute("project", new ProjectWriteModel());
        model.addAttribute("projects",getProjects());
        model.addAttribute("message", "Dodano projekt");
        return showProject(model);
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
