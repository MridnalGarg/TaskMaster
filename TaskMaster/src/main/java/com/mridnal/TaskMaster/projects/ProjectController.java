package com.mridnal.TaskMaster.projects;

import com.mridnal.TaskMaster.projects.dto.CreateProjectRequest;
import com.mridnal.TaskMaster.projects.dto.ProjectResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projects")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @PostMapping
    public ProjectResponse createProject(@Valid @RequestBody CreateProjectRequest request, Authentication auth) {

        if(request.name().isEmpty() || request.name().length() < 8) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Name is required! (minimum 8 Characters)"
            );
        }

        if(!request.description().isEmpty() && request.description().length() < 8) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Description should have minimum 8 Characters"
            );
        }

        return ProjectResponse.from(
                projectService.createProject(request, auth)
        );
    }

    @GetMapping("/me")
    public List<ProjectResponse> getMyProjects(Authentication auth){
        return projectService.getMyProjects(auth);
    }

    @DeleteMapping
    public void deleteProject(@RequestBody Projects project) {
        projectService.deleteProject(project);
    }

}
