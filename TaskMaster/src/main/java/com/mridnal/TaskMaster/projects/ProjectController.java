package com.mridnal.TaskMaster.projects;

import com.mridnal.TaskMaster.projects.dto.CreateProjectRequest;
import com.mridnal.TaskMaster.projects.dto.ProjectResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/projects")
@Tag(name = "Project Controller", description = "Project Management")
public class ProjectController {

    @Autowired
    private ProjectService projectService;

    @PostMapping
    @Operation(summary = "Create a project")
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
    @Operation(summary = "Get my projects (For Authenticated user)")
    public List<ProjectResponse> getMyProjects(Authentication auth){
        return projectService.getMyProjects(auth);
    }

    @DeleteMapping
    @Operation(summary = "Delete a project")
    public void deleteProject(@RequestBody Projects project) {
        projectService.deleteProject(project);
    }

    @GetMapping("/invite")
    @Operation(summary = "Send Project Invite")
    public ResponseEntity<String> sendInvite(
            Authentication auth,
            Long inviteeId,
            Long projectId) {
        return ResponseEntity.ok(
                projectService.inviteUserToProject(inviteeId, projectId, auth)
        );
    }

    @PostMapping("/invitations/accept")
    @Operation(summary = "Accept invite")
    public ResponseEntity<String> acceptInvite(
            @RequestParam String code,
            Authentication authentication
    ) {
        projectService.acceptInvite(code, authentication);

        return ResponseEntity.ok(
                "Project invitation accepted successfully"
        );
    }
}
