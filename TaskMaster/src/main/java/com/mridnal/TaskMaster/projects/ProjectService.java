package com.mridnal.TaskMaster.projects;

import com.mridnal.TaskMaster.projects.dto.CreateProjectRequest;
import com.mridnal.TaskMaster.projects.dto.ProjectResponse;
import com.mridnal.TaskMaster.projects.memberships.ProjectMembershipRepository;
import com.mridnal.TaskMaster.projects.memberships.ProjectMemberships;
import com.mridnal.TaskMaster.users.UserService;
import com.mridnal.TaskMaster.users.Users;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectMembershipRepository projectMembershipRepository;

    @Autowired
    private UserService userService;

    public Projects createProject(CreateProjectRequest request, Authentication auth) {

        Users owner = userService.getCurrentUser(auth);

        if(owner == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Owner not found"
            );
        }

        Projects project = new Projects(
                request.name(),
                request.description(),
                owner
        );


        Projects savedProject = projectRepository.save(project);

        ProjectMemberships memberships = new ProjectMemberships(
                savedProject,
                owner
        );

        projectMembershipRepository.save(memberships);

        return savedProject;
    }

    public void deleteProject(Projects project) {
        projectRepository.delete(project);
    }

    @Transactional
    public List<ProjectResponse> getMyProjects(Authentication auth) {

        Users user = userService.getCurrentUser(auth);

        if (user == null) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "User not found"
            );
        }

        List<Projects> projects = projectRepository.findProjectsByUserId(user.getId());

        return projects.stream()
                .map(ProjectResponse::from)
                .toList();
    }

    public Projects getById(Long id){
        return projectRepository.findById(id)
                .orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found")
                );
    }
}
