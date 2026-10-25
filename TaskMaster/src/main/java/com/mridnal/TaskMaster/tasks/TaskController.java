package com.mridnal.TaskMaster.tasks;

import com.mridnal.TaskMaster.enums.TaskStatus;
import com.mridnal.TaskMaster.tasks.dto.TaskCreateRequest;
import com.mridnal.TaskMaster.tasks.dto.TaskResponse;
import com.mridnal.TaskMaster.tasks.dto.TaskUpdateRequest;
import com.mridnal.TaskMaster.users.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Tag(name = "Tasks", description = "Task management")
@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

    @Autowired
    private TasksService taskService;

    @Operation(
            summary = "Create task in a project"
    )
    @PostMapping
    public TaskResponse createTask(@Valid @RequestBody TaskCreateRequest request, Authentication auth) {
        return TaskResponse.from(
            taskService.createTask(request, auth)
        );
    }

    @Operation(
            summary = "Update tasks Details"
    )
    @PutMapping
    public TaskResponse updateTaskDetails(@Valid @RequestBody TaskUpdateRequest request, Authentication auth) {
        return TaskResponse.from(
                taskService.updateTaskDetails(auth, request)
        );
    }

    @Operation(
            summary = "Get tasks in project",
            description = "Returns tasks created in specific Project to the authenticated user. Supports status filtering and text search."
    )
    @GetMapping("/project/{projectId}")
    public List<TaskResponse> getAllTasksInProject(
            Authentication auth,
            @PathVariable Long projectId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search) {

        List<Tasks> tasks = taskService.findAllTasksInProject(
                auth,
                projectId,
                status,
                search);

        return tasks.stream()
                .map(TaskResponse::from)
                .toList();
    }

    @Operation(
            summary = "Update Task Status",
            description = "Updates the TaskStatus and returns response object."
    )
    @PatchMapping("/{taskId}/status")
    public TaskResponse updateTaskStatus(
            @PathVariable("taskId") Long taskId,
            @RequestParam("taskStatus") TaskStatus taskStatus
    ) {
        return TaskResponse.from(
            taskService.updateTaskStatus(taskId, taskStatus)
        );
    }

    @Operation(
            summary = "Get tasks assigned to me",
            description = "Returns tasks assigned to the authenticated user. Supports status filtering and text search."
    )
    @GetMapping("/assigned/me")
    public List<TaskResponse> getAllTasksAssignedToMe(
            Authentication authentication,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search) {

        List<Tasks> tasks = taskService.findAllTasksAssignedToMe(
                authentication,
                status,
                search);

        return tasks.stream()
                .map(TaskResponse::from)
                .toList();
    }

    @Operation(summary = "Assign the task to user")
    @PutMapping("/{taskId}/assign")
    public TaskResponse assignTask(
            @PathVariable("taskId") Long taskId,
            @RequestParam("assigneeId") Long assigneeId) {

        if(taskId == null || assigneeId == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Some fields are missing."
            );
        }

        return taskService.assignTaskToProjectMember(taskId, assigneeId);
    }


}
