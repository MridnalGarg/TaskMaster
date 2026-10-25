package com.mridnal.TaskMaster.projects;

import com.mridnal.TaskMaster.projects.dto.CreateProjectRequest;
import com.mridnal.TaskMaster.projects.dto.ProjectResponse;
import com.mridnal.TaskMaster.projects.memberships.ProjectMembershipRepository;
import com.mridnal.TaskMaster.projects.memberships.ProjectMemberships;
import com.mridnal.TaskMaster.projects.memberships.ProjectMembershipsService;
import com.mridnal.TaskMaster.users.UserService;
import com.mridnal.TaskMaster.users.Users;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Service
public class ProjectService {

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ProjectMembershipRepository projectMembershipRepository;

    @Autowired
    private ProjectMembershipsService projectMembershipsService;

    @Autowired
    private UserService userService;

    @Autowired
    private InvitationTokenService invitationTokenService;

    @Value("${app.base-url}")
    private String baseUrl;

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

    public String inviteUserToProject(
            Long inviteeId,
            Long projectId,
            Authentication authentication
    ) {
        Users inviter = userService.getCurrentUser(authentication);

        Users invitee = userService.findByUserId(inviteeId);

        Projects project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Project not found"
                ));

        if (project.getOwner() == null
                || !project.getOwner().getId().equals(inviter.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Only the project owner can invite users"
            );
        }

        if (inviter.getId().equals(invitee.getId())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "You cannot invite yourself"
            );
        }

        if (projectMembershipRepository.existsByProjectAndUser(project, invitee)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "User is already a member of this project"
            );
        }

        String inviteToken = invitationTokenService.generateToken(
                invitee.getId(),
                project.getId()
        );

        return baseUrl
                + "/projects/invitations/accept?code="
                + URLEncoder.encode(
                "Bearer " + inviteToken,
                StandardCharsets.UTF_8
        );
    }

    @Transactional
    public void acceptInvite(
            String code,
            Authentication authentication
    ) {
        Users currentUser = userService.getCurrentUser(authentication);

        if (code == null || code.isBlank()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invitation code is required"
            );
        }

        // Accept either "Bearer <token>" or the raw JWT.
        String token = code.trim();

        if (token.regionMatches(true, 0, "Bearer ", 0, 7)) {
            token = token.substring(7).trim();
        }

        Claims claims;

        try {
            claims = invitationTokenService.validateToken(token);
        } catch (JwtException | IllegalArgumentException ex) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid or expired invitation token"
            );
        }

        Long inviteeId = Long.valueOf(claims.getSubject());
        Long projectId = claims.get("projectId", Long.class);

        if (!currentUser.getId().equals(inviteeId)) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "This invitation belongs to another user"
            );
        }

        Projects project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Project not found"
                ));

        if (projectMembershipRepository
                .existsByProjectAndUser(project, currentUser)) {
            return;
        }

        projectMembershipsService
                .createProjectMembership(project, currentUser);
    }
}
