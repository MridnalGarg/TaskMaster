package com.mridnal.TaskMaster.tasks;

import com.mridnal.TaskMaster.enums.TaskStatus;
import com.mridnal.TaskMaster.projects.ProjectRepository;
import com.mridnal.TaskMaster.projects.ProjectService;
import com.mridnal.TaskMaster.projects.Projects;
import com.mridnal.TaskMaster.projects.memberships.ProjectMembershipRepository;
import com.mridnal.TaskMaster.tasks.dto.TaskCreateRequest;
import com.mridnal.TaskMaster.tasks.dto.TaskResponse;
import com.mridnal.TaskMaster.tasks.dto.TaskUpdateRequest;
import com.mridnal.TaskMaster.users.UserService;
import com.mridnal.TaskMaster.users.Users;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
public class TasksService {

    @Autowired
    private TasksRepository tasksRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectMembershipRepository projectMembershipRepository;

    @Autowired
    private ProjectService projectService;

    private TaskStatus parseStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }

        try {
            return TaskStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Task status '" + status + "' is not valid"
            );
        }
    }

    private String normalizeSearch(String search) {
        if (search == null || search.isBlank()) {
            return "";
        }

        return search.trim();
    }

    @Transactional
    public List<Tasks> findAllTasksInProject(
            Authentication auth,
            Long projectId,
            String status,
            String search) {

        Users user = userService.getCurrentUser(auth);

        if (user == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Authenticated user not found"
            );
        }

        Projects project = projectService.getById(projectId);

        if (!projectMembershipRepository.existsByProjectAndUser(project, user)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "User is not a member of this project"
            );
        }

        TaskStatus taskStatus = parseStatus(status);
        String searchTerm = normalizeSearch(search);

        return tasksRepository.findAllTasksInProject(
                projectId,
                taskStatus,
                searchTerm
        );
    }

    @Transactional
    public List<Tasks> findAllTasksAssignedToMe(
            Authentication authentication,
            String status,
            String search) {

        Users user = userService.getCurrentUser(authentication);

        if (user == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Authenticated user not found"
            );
        }

        return tasksRepository.findAssignedTasks(
                user.getId(),
                parseStatus(status),
                normalizeSearch(search)
        );
    }

    @Transactional
    public Tasks createTask(TaskCreateRequest request, Authentication auth) {

        if (request.title() == null || request.title().isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Title is required!"
            );
        }

        if (request.dueDate() == null
                || request.dueDate().before(Date.valueOf(LocalDate.now()))) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Due date cannot be in the past"
            );
        }

        if (request.projectId() == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Project ID is required"
            );
        }

        Users reporter = userService.getCurrentUser(auth);

        if (reporter == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Authenticated user not found"
            );
        }

        Projects project = projectService.getById(request.projectId());

        Users assignee = request.assigneeId() == null
                ? reporter
                : userService.findByUserId(request.assigneeId());

        Tasks task = new Tasks(
                request.title().trim(),
                request.description().trim(),
                request.dueDate(),
                reporter,
                assignee,
                project
        );

        return tasksRepository.save(task);
    }

    @Transactional
    public Tasks updateTaskDetails(Authentication auth, TaskUpdateRequest request) {
        Users user = userService.findUserByEmail(auth.getName());

        if (!Objects.equals(user.getId(), request.reporterId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "User not authenticated."
            );
        }

        Tasks task = tasksRepository.findById(request.taskId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Task with id " + request.taskId() + " not found"
                ));

        if (!request.title().isEmpty()) {
            task.setTitle(request.title());
        }
        if (!request.description().isEmpty()) {
            task.setDescription(request.description());
        }
        if (!request.dueDate().before(Date.valueOf(LocalDate.now()))) {
            task.setDueDate(Date.valueOf(LocalDate.now()));
        }

        Users assignee = userService.findByUserId(request.assigneeId());

        task.setAssignee(assignee);

        return tasksRepository.save(task);
    }

    @Transactional
    public Tasks updateTaskStatus(Long id, TaskStatus taskStatus) {
        Tasks task = tasksRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Task with id " + id + " not found."
                ));

        if (taskStatus.equals(task.getStatus())) {
            return task;
        }

        task.setStatus(taskStatus);

        return tasksRepository.save(task);
    }

    @Transactional
    public TaskResponse assignTaskToProjectMember(Long taskId, Long assigneeId) {

        Users assignee = userService.findByUserId(assigneeId);

        Tasks task = tasksRepository.findById(taskId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Task with id " + taskId + " not found"
                ));

        boolean canAssign = projectMembershipRepository.existsByProjectAndUser(
                task.getProject(),
                assignee
        );

        if(!canAssign) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "User " + assignee.getName() + " is not authorized to this project."
            );
        }

        task.setAssignee(assignee);

        Tasks saved = tasksRepository.save(task);

        return TaskResponse.from(saved);
    }

}
